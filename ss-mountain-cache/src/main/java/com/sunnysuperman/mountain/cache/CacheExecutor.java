package com.sunnysuperman.mountain.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CacheExecutor {

	byte[] find(String key, CachePolicy policy) throws CacheException;

	Map<String, byte[]> findMany(List<String> keys, CachePolicy policy) throws CacheException;

	void save(String key, byte[] value, CachePolicy policy) throws CacheException;

	void saveMany(Map<String, byte[]> items, CachePolicy policy) throws CacheException;

	void remove(String key) throws CacheException;

	void removeMany(Collection<String> keys) throws CacheException;

	Long incrbyIfExists(String key, long num) throws CacheException;

	Double incrbyIfExists(String key, double num) throws CacheException;

}
