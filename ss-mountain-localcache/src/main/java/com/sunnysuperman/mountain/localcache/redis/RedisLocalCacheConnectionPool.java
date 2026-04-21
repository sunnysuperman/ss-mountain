package com.sunnysuperman.mountain.localcache.redis;

import java.time.Duration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisLocalCacheConnectionPool {

	RedisLocalCacheProperties props;
	private JedisPool pool;

	public RedisLocalCacheConnectionPool(RedisLocalCacheProperties props) {
		super();
		this.props = props;
		this.pool = createPool(props);
	}

	public RedisLocalCacheProperties getProps() {
		return props;
	}

	public JedisPool getPool() {
		return pool;
	}

	public Jedis getConnection() {
		return pool.getResource();
	}

	private static JedisPool createPool(RedisLocalCacheProperties p) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setJmxEnabled(false);
		config.setMinIdle(p.getMinIdle());
		config.setMaxIdle(p.getMaxIdle());
		config.setMaxTotal(p.getMaxTotal());
		config.setMaxWait(Duration.ofSeconds(p.getMaxWait()));
		config.setMinEvictableIdleTime(Duration.ofSeconds(p.getIdleTime()));
		return new JedisPool(config, p.getHost(), p.getPort(), Protocol.DEFAULT_TIMEOUT, p.getPassword(), p.getDb());
	}

}
