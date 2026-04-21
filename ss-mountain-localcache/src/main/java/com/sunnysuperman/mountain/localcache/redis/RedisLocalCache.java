package com.sunnysuperman.mountain.localcache.redis;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.utils.ProcessUtil;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.localcache.LocalCache;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/** Local cache implementation based on the Redis Pub/Sub model **/
public class RedisLocalCache<T> implements LocalCache<T> {

	private static final Logger LOG = LoggerFactory.getLogger(RedisLocalCache.class);
	private static final String CYCLIC_INCREMENT_SCRIPT = String.join(" ",
			"local current = tonumber(redis.call('GET', KEYS[1]) or '0')", "local increment = tonumber(ARGV[1])",
			"local newValue = current + increment", "if newValue > 100000 then", "    newValue = newValue % 100001",
			"end", "redis.call('SET', KEYS[1], newValue)", "return newValue");
	private static final byte[] CONNECTION_LOCK = new byte[0];
	private boolean stopped;

	private String key;
	private Supplier<T> supplier;
	private RedisLocalCacheConnectionPool connectionPool;
	private T value;
	private Jedis subscriptionConnection;
	private long version = -1;

	public RedisLocalCache(String key, Supplier<T> supplier, RedisLocalCacheConnectionPool connectionPool) {
		super();
		this.key = key;
		this.supplier = supplier;
		this.connectionPool = connectionPool;
	}

	public String getKey() {
		return key;
	}

	public Supplier<T> getSupplier() {
		return supplier;
	}

	@Override
	public T find() {
		if (value == null) {
			if (LOG.isInfoEnabled()) {
				LOG.info("[RedisLocalCache] init: {}", key);
			}
			doUpdate();
		}
		return value;
	}

	@Override
	public void update() {
		try (Jedis connection = connectionPool.getConnection()) {
			Object newVersion = connection.eval(CYCLIC_INCREMENT_SCRIPT, List.of(versionKey()), List.of("1"));
			Long consumersNum = connection.publish(key, newVersion.toString());
			if (LOG.isInfoEnabled()) {
				LOG.info("[RedisLocalCache] {} consumers received changes: {} - {}", consumersNum, key, newVersion);
			}
		}
	}

	@Override
	public void reload() {
		if (LOG.isInfoEnabled()) {
			LOG.info("[RedisLocalCache] reload: {}", key);
		}
		doUpdate();
	}

	public void start(boolean load) {
		// initialize data
		if (load) {
			find();
		}
		// subscription uses an independent connection
		new Thread(this::subscribe).start();
	}

	public void stop() {
		synchronized (CONNECTION_LOCK) {
			stopped = true;
			doClose();
		}
	}

	private void subscribe() {
		while (!stopped) {
			try {
				doSubscribe();
			} catch (Exception ex) {
				if (!(ex.getCause() instanceof SocketTimeoutException)) {
					LOG.error(null, ex);
				}
				LOG.warn("[RedisLocalCache] subscription disconnected, will retry to connect later");
				ProcessUtil.sleep(1000);
			}
			// reload data on subscription connection reset
			if (!stopped) {
				try {
					reload();
				} catch (Exception ex) {
					LOG.error(null, ex);
				}
			}
		}
	}

	private void doSubscribe() {
		if (LOG.isInfoEnabled()) {
			LOG.info("[RedisLocalCache] ready to subscribe");
		}
		synchronized (CONNECTION_LOCK) {
			if (stopped) {
				LOG.warn("[RedisLocalCache] subscription stopped");
				return;
			}
			doClose();
			subscriptionConnection = createConnection();
		}
		subscriptionConnection.subscribe(new JedisPubSub() {

			@Override
			public void onMessage(String channel, String message) {
				if (LOG.isInfoEnabled()) {
					LOG.info("[RedisLocalCache] received changes: {}", key);
				}
				doUpdate();
			}

		}, key);
	}

	private void doClose() {
		if (subscriptionConnection != null) {
			try {
				subscriptionConnection.close();
			} catch (Exception ex) {
				LOG.error(null, ex);
			}
		}
	}

	private synchronized void doUpdate() {
		// compare version to avoid useless data update
		long currentVersion = getCurrentVersion();
		if ((value != null) && (version == currentVersion)) {
			if (LOG.isInfoEnabled()) {
				LOG.info("[RedisLocalCache] no changes: {}", key);
			}
			return;
		}
		if (LOG.isInfoEnabled()) {
			LOG.info("[RedisLocalCache] doUpdate {}: {} -> {}", key, version, currentVersion);
		}
		// update data
		value = supplier.get();
		// update version
		version = currentVersion;
	}

	private long getCurrentVersion() {
		try (Jedis connection = connectionPool.getConnection()) {
			return Long.parseLong(Str.or(connection.get(versionKey()), "0"));
		}
	}

	private String versionKey() {
		return key + "_v";
	}

	private Jedis createConnection() {
		RedisLocalCacheProperties props = connectionPool.getProps();
		return new Jedis(new HostAndPort(props.getHost(), props.getPort()),
				DefaultJedisClientConfig.builder().password(props.getPassword()).database(props.getDb())
						.connectionTimeoutMillis(5000).blockingSocketTimeoutMillis(600 * 1000).build());
	}

}
