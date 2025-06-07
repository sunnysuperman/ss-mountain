package com.sunnysuperman.mountain.cache.redis;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisClient {
	private static final Logger LOG = LoggerFactory.getLogger(RedisClient.class);

	public static interface RedisWork<T> {
		T run(Jedis jedis);
	}

	private static class TestRedisWork implements RedisWork<Void> {

		@Override
		public Void run(Jedis jedis) {
			jedis.expire("0", 1L);
			return null;
		}

	}

	private JedisPool pool;

	public RedisClient(JedisPool pool, boolean testOnStart) {
		this.pool = pool;
		if (testOnStart) {
			execute(new TestRedisWork());
		}
	}

	public RedisClient(JedisPool pool) {
		this(pool, true);
	}

	public <T> T execute(RedisWork<T> work) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return work.run(jedis);
		} finally {
			close(jedis);
		}
	}

	public Object executeScript(String script, List<String> keys, List<String> args) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.eval(script, keys != null ? keys : Collections.emptyList(),
					args != null ? args : Collections.emptyList());
		} finally {
			close(jedis);
		}
	}

	public void expire(String key, long seconds) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.expire(key, seconds);
		} finally {
			close(jedis);
		}
	}

	public boolean exists(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.exists(key);
		} finally {
			close(jedis);
		}
	}

	public boolean del(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.del(key) > 0;
		} finally {
			close(jedis);
		}
	}

	public long del(String[] keys) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.del(keys);
		} finally {
			close(jedis);
		}
	}

	public void set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.set(key, value);
		} finally {
			close(jedis);
		}
	}

	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.get(key);
		} finally {
			close(jedis);
		}
	}

	public String lpop(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.lpop(key);
		} finally {
			close(jedis);
		}
	}

	public long llen(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.llen(key);
		} finally {
			close(jedis);
		}
	}

	public String lindex(String key, long index) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.lindex(key, index);
		} finally {
			close(jedis);
		}
	}

	public long rpush(String key, String[] values) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.rpush(key, values);
		} finally {
			close(jedis);
		}
	}

	public long scard(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.scard(key);
		} finally {
			close(jedis);
		}
	}

	public long sadd(String key, String[] values) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.sadd(key, values);
		} finally {
			close(jedis);
		}
	}

	public boolean srem(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.srem(key, value) > 0;
		} finally {
			close(jedis);
		}
	}

	public String spop(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.spop(key);
		} finally {
			close(jedis);
		}
	}

	private void close(Jedis jedis) {
		if (jedis != null) {
			try {
				jedis.close();
			} catch (Exception e) {
				LOG.error("Failed to close redis client", e);
			}
		}
	}
}
