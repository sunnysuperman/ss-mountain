package com.sunnysuperman.mountain.cache.redis;

import java.time.Duration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisUtils {

	private RedisUtils() {
	}

	public static JedisPool createPool(RedisProperties cfg) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setJmxEnabled(false);
		config.setMinIdle(cfg.getMinIdle());
		config.setMaxIdle(cfg.getMaxIdle());
		config.setMaxTotal(cfg.getMaxTotal());
		config.setMaxWait(Duration.ofSeconds(cfg.getMaxWait()));
		config.setMinEvictableIdleTime(Duration.ofSeconds(cfg.getIdleTime()));
		return new JedisPool(config, cfg.getHost(), cfg.getPort(), Protocol.DEFAULT_TIMEOUT, cfg.getPassword(),
				cfg.getDb());
	}
}
