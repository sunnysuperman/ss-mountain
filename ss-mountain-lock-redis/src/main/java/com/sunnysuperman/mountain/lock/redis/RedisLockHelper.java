package com.sunnysuperman.mountain.lock.redis;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lock.LockHelper;

public class RedisLockHelper implements LockHelper {
	private static final Logger LOG = LoggerFactory.getLogger(RedisLockHelper.class);

	private RedissonClient client;
	private String keyPrefix;

	public RedisLockHelper(RedisLockProperties properties, EnvHelper envHelper) {
		Config config = new Config();
		SingleServerConfig singleSerververConfig = config.useSingleServer();
		singleSerververConfig.setAddress("redis://" + properties.getHost() + ":" + properties.getPort());
		singleSerververConfig.setPassword(properties.getPassword());
		singleSerververConfig.setDatabase(properties.getDb());
		singleSerververConfig.setConnectionMinimumIdleSize(properties.getMinIdle());
		singleSerververConfig.setConnectionPoolSize(properties.getMaxTotal());
		client = Redisson.create(config);
		keyPrefix = getPrefix(properties, envHelper);
		if (LOG.isInfoEnabled()) {
			LOG.info(">>>>>>[lock-redis] initialized, keyPrefix: '{}'", keyPrefix);
		}
	}

	@Override
	public boolean tryLock(List<String> keys, Runnable work, int timeout, int leaseTime, boolean ignoreLockError) {
		if (keys == null || keys.isEmpty()) {
			throw new IllegalArgumentException("keys");
		}
		List<RLock> locks = keys.stream().map(this::wrapKey).map(key -> client.getLock(key))
				.collect(Collectors.toList());
		RLock lock = client.getMultiLock(locks.toArray(new RLock[locks.size()]));
		return doWork(lock, work, timeout, leaseTime, ignoreLockError);
	}

	@Override
	public boolean tryLock(String key, Runnable work, int timeout, int leaseTime, boolean ignoreLockError) {
		key = wrapKey(key);
		if (leaseTime <= 0) {
			leaseTime = getDefaultLeaseTime();
		}
		RLock lock = client.getLock(key);
		return doWork(lock, work, timeout, leaseTime, ignoreLockError);
	}

	// 默认超时释放时间
	protected int getDefaultLeaseTime() {
		return 300;
	}

	private boolean doWork(RLock lock, Runnable work, int timeout, int leaseTime, boolean ignoreLockError) {
		if (leaseTime <= 0) {
			leaseTime = getDefaultLeaseTime();
		}
		boolean lockGetted = false;
		try {
			lockGetted = lock.tryLock(timeout, leaseTime, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			lockGetted = ignoreLockError;
		} catch (Exception e) {
			LOG.error("Failed to get lock", e);
			lockGetted = ignoreLockError;
		}
		if (!lockGetted) {
			return false;
		}
		try {
			work.run();
			return true;
		} finally {
			lock.unlock();
		}
	}

	private String wrapKey(String key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("key is empty");
		}
		if (keyPrefix == null) {
			return key;
		}
		return new StringBuilder(keyPrefix.length() + key.length()).append(keyPrefix).append(key).toString();
	}

	private String getPrefix(RedisLockProperties properties, EnvHelper envHelper) {
		StringBuilder b = new StringBuilder();
		if (properties.isUseProfileAsNamespace()) {
			b.append(Str.or(properties.getProfile(), envHelper.getProfile())).append(':');
		}
		if (properties.isUseAppNameAsNamespace()) {
			b.append(Str.or(properties.getAppName(), envHelper.getApplicationName())).append(':');
		}
		return Str.emptyToNull(b.toString());
	}
}
