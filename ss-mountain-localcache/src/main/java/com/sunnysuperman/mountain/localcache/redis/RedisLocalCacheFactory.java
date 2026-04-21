package com.sunnysuperman.mountain.localcache.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.lang.utils.Retryer;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.localcache.LocalCache;

public class RedisLocalCacheFactory {

	private static final Logger LOG = LoggerFactory.getLogger(RedisLocalCacheFactory.class);

	private RedisLocalCacheConnectionPool connectionPool;
	private EnvHelper envHelper;
	private Set<String> registeredKeys = new HashSet<>();
	private List<RedisLocalCache<?>> cacheList = new ArrayList<>();
	private ScheduledExecutorService scheduler;

	public RedisLocalCacheFactory(RedisLocalCacheProperties props, EnvHelper envHelper) {
		super();
		this.connectionPool = new RedisLocalCacheConnectionPool(props);
		this.envHelper = envHelper;
	}

	public synchronized <T> LocalCache<T> create(String key, Supplier<T> supplier, boolean loadOnCreated,
			int reloadIntervalInSeconds) {
		if (Str.isEmpty(key)) {
			throw new IllegalArgumentException("Bad cache key");
		}
		if (!registeredKeys.add(key)) {
			throw new IllegalArgumentException("Duplicate cache key: " + key);
		}
		String canonicalKey = getPrefix(connectionPool.getProps()) + key;
		RedisLocalCache<T> cache = new RedisLocalCache<>(canonicalKey, supplier, connectionPool);
		cacheList.add(cache);
		// 启动
		cache.start(loadOnCreated);
		// 定时更新
		if (reloadIntervalInSeconds > 0) {
			scheduleReloading(cache, reloadIntervalInSeconds);
		}
		return cache;
	}

	public synchronized void stopAll() {
		cacheList.forEach(RedisLocalCache::stop);
	}

	private String getPrefix(RedisLocalCacheProperties props) {
		StringBuilder b = new StringBuilder();
		if (props.isUseProfileAsNamespace()) {
			b.append(Str.or(props.getProfile(), envHelper.getProfile())).append(':');
		}
		if (props.isUseAppNameAsNamespace()) {
			b.append(Str.or(props.getAppName(), envHelper.getApplicationName())).append(':');
		}
		return b.toString();
	}

	private void scheduleReloading(RedisLocalCache<?> cache, int reloadIntervalInSeconds) {
		if (scheduler == null) {
			scheduler = Executors.newScheduledThreadPool(1);
		}
		scheduler.scheduleWithFixedDelay(() -> {
			try {
				new Retryer().setMaxAttempts(3).setRetryDelayInMills(3000).call(() -> {
					cache.reload();
					return null;
				});
			} catch (Exception ex) {
				LOG.error("Failed to reload local cache: " + cache.getKey(), ex);
			}
		}, reloadIntervalInSeconds, reloadIntervalInSeconds, TimeUnit.SECONDS);
	}

}
