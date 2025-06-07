package com.sunnysuperman.mountain.cache;

import java.util.Collection;
import java.util.Map;

public interface Cache<T, K> {

	/**
	 * 查询，如果没有会从数据源获取数据，然后加载到缓存中
	 * 
	 * @param key       键
	 * @param cacheOnly 是否仅从缓存加载
	 * @return 值
	 */
	T findByKey(K key, boolean cacheOnly);

	/**
	 * 查询，如果没有会从数据源获取数据，然后加载到缓存中
	 * 
	 * @param key 键
	 * @return 值
	 **/
	default T findByKey(K key) {
		return findByKey(key, false);
	}

	/**
	 * 批量查询，如果没有会从数据源获取数据，然后加载到缓存中
	 * 
	 * @param keys      多个键
	 * @param cacheOnly 是否仅从缓存加载
	 * @return 键值映射
	 **/
	Map<K, T> findByKeys(Collection<K> keys, boolean cacheOnly);

	/**
	 * 批量查询，如果没有会从数据源获取数据，然后加载到缓存中
	 * 
	 * @param keys 多个键
	 * @return 键值映射
	 **/
	default Map<K, T> findByKeys(Collection<K> keys) {
		return findByKeys(keys, false);
	}

	/**
	 * 增加整数值(仅限指定缓存存在)
	 * 
	 * @param key 键
	 * @param num 增加值，负数为减少
	 * @return 新值
	 **/
	Long incrbyIfExists(K key, long num) throws CacheException;

	/**
	 * 增加浮点值(仅限指定缓存存在)
	 * 
	 * @param key 键
	 * @param num 增加值，负数为减少
	 * @return 新值
	 **/
	Double incrbyIfExists(K key, double num) throws CacheException;

	/** 安全保存 **/
	void save(K key, T value);

	/** 尝试保存，出错会抛出异常 **/
	void tryToSave(K key, T value) throws CacheException;

	/** 批量安全保存 **/
	void saveMany(Map<K, T> items);

	/** 尝试批量保存，出错会抛出异常 **/
	void tryToSaveMany(Map<K, T> items) throws CacheException;

	/** 安全删除 **/
	void remove(K key);

	/** 尝试删除，出错会抛出异常 **/
	void tryToRemove(K key) throws CacheException;

	/** 安全批量删除 **/
	void removeMany(Collection<K> keys);

	/** 尝试批量删除，出错会抛出异常 **/
	void tryToRemoveMany(Collection<K> keys) throws CacheException;

	/** 刷新缓存（重新加载内容，写入缓存） **/
	void refresh(K key);

	/** 尝试刷新缓存（重新加载内容，写入缓存并返回），出错会抛出异常 **/
	T tryToRefresh(K key) throws CacheException;

}
