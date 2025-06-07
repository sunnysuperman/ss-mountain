package com.sunnysuperman.mountain.cache.redis;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.cache.CacheExecutor;
import com.sunnysuperman.mountain.cache.CachePolicy;
import com.sunnysuperman.mountain.lang.utils.Num;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.SafeEncoder;

public class RedisCacheExecutor implements CacheExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(RedisCacheExecutor.class);
	private static final String INCREASE_IF_EXISTS = "local v=redis.call('exists', KEYS[1]);if(v==0) then return nil;else return redis.call('incrby', KEYS[1], ARGV[1]);end";
	private static final String INCREASE_FLOAT_IF_EXISTS = "local v=redis.call('exists', KEYS[1]);if(v==0) then return nil;else return redis.call('incrbyfloat', KEYS[1], ARGV[1]);end";

	protected JedisPool pool;

	public RedisCacheExecutor(JedisPool pool) {
		super();
		this.pool = pool;
	}

	public JedisPool getPool() {
		return pool;
	}

	private void close(Jedis jedis) {
		if (jedis != null) {
			try {
				jedis.close();
			} catch (Exception ex) {
				LOG.error(null, ex);
			}
		}
	}

	@Override
	public byte[] find(String key, CachePolicy policy) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			return jedis.get(SafeEncoder.encode(key));
		} finally {
			close(jedis);
		}
	}

	@Override
	public Map<String, byte[]> findMany(List<String> keys, CachePolicy policy) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			byte[][] bkeys = new byte[keys.size()][0];
			int i = 0;
			for (String key : keys) {
				bkeys[i] = SafeEncoder.encode(key);
				i++;
			}
			List<byte[]> values = jedis.mget(bkeys);
			i = -1;
			Map<String, byte[]> kv = new HashMap<>();
			for (String key : keys) {
				i++;
				byte[] value = values.get(i);
				if (value == null) {
					continue;
				}
				kv.put(key, value);
			}
			return kv;
		} finally {
			close(jedis);
		}
	}

	@Override
	public void save(String key, byte[] value, CachePolicy policy) {
		Jedis jedis = null;
		long expireIn = policy.getExpireIn();
		try {
			jedis = pool.getResource();
			jedis.setex(SafeEncoder.encode(key), expireIn, value);
		} finally {
			close(jedis);
		}
	}

	@Override
	public void saveMany(Map<String, byte[]> items, CachePolicy policy) {
		Jedis jedis = null;
		long expireIn = policy.getExpireIn();
		try {
			jedis = pool.getResource();
			for (Entry<String, byte[]> item : items.entrySet()) {
				jedis.setex(SafeEncoder.encode(item.getKey()), expireIn, item.getValue());
			}
		} finally {
			close(jedis);
		}
	}

	@Override
	public void remove(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.del(key);
		} finally {
			close(jedis);
		}
	}

	@Override
	public void removeMany(Collection<String> keys) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.del(keys.toArray(new String[keys.size()]));
		} finally {
			close(jedis);
		}
	}

	@Override
	public Long incrbyIfExists(String key, long num) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			Object result = jedis.eval(INCREASE_IF_EXISTS, Arrays.asList(key),
					Collections.singletonList(String.valueOf(num)));
			return Num.parseLong(result);
		} finally {
			close(jedis);
		}
	}

	@Override
	public Double incrbyIfExists(String key, double num) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			Object result = jedis.eval(INCREASE_FLOAT_IF_EXISTS, Arrays.asList(key),
					Collections.singletonList(String.valueOf(num)));
			return Num.parseDouble(result);
		} finally {
			close(jedis);
		}
	}

}
