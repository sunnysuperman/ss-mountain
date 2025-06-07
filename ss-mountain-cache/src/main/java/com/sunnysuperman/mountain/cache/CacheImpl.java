package com.sunnysuperman.mountain.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.cache.converter.Converter;
import com.sunnysuperman.mountain.cache.provider.RepositoryProvider;
import com.sunnysuperman.mountain.task.ScheduledTaskExecutor;

public class CacheImpl<T, K> implements Cache<T, K> {
	private static final Logger LOG = LoggerFactory.getLogger(CacheImpl.class);
	private static final boolean INFO_ENABLED = LOG.isInfoEnabled();
	// 缓存更新失败后，指定时间再次刷新缓存
	private static final int ERROR_RETRY_DELAY = 8000;
	// 并发更新，指定时间再次刷新缓存
	private static final int CONCURRENT_REFRESH_DELAY = 5000;

	private CacheExecutor executor;
	private CachePolicy policy;
	private RepositoryProvider<T, K> repository;
	private Converter<T> converter;
	private CacheSaveFilter<T, K> saveFilter;
	private ScheduledTaskExecutor scheduledTaskExecutor;

	public CacheImpl(CacheExecutor executor, CachePolicy policy, RepositoryProvider<T, K> repository,
			Converter<T> converter, CacheSaveFilter<T, K> saveFilter, ScheduledTaskExecutor scheduledTaskExecutor) {
		super();
		this.executor = executor;
		this.policy = policy;
		this.repository = repository;
		this.converter = converter;
		this.saveFilter = saveFilter;
		this.scheduledTaskExecutor = scheduledTaskExecutor;
	}

	@Override
	public T findByKey(K key, boolean cacheOnly) {
		if (key == null) {
			return null;
		}
		T value = null;
		// 从缓存中查找
		try {
			value = doFind(key);
		} catch (Exception e) {
			LOG.error(null, e);
		}
		if (value != null) {
			return value;
		}
		if (cacheOnly) {
			return null;
		}
		// 缓存未命中，从存储层查询
		value = repository.findByKey(key);
		// 保存到缓存
		if (value != null) {
			try {
				doSave(key, value);
			} catch (Exception e) {
				LOG.error(null, e);
			}
		}
		// 返回
		return value;
	}

	@Override
	public Map<K, T> findByKeys(Collection<K> keys, boolean cacheOnly) {
		if (keys == null || keys.isEmpty()) {
			return Collections.emptyMap();
		}
		// 从缓存中查找
		Map<K, T> map = null;
		try {
			map = doFind(keys);
		} catch (Exception e) {
			LOG.error(null, e);
			map = new HashMap<>();
		}
		// 指定只从缓存中获取 或者 命中缓存
		if (cacheOnly || map.size() == keys.size()) {
			return map;
		}
		List<K> queryKeys = new ArrayList<>(Math.max(keys.size() - map.size(), 1));
		for (K key : keys) {
			if (!map.containsKey(key)) {
				queryKeys.add(key);
			}
		}
		if (queryKeys.isEmpty()) {
			return map;
		}
		// 从存储层中查找
		Map<K, T> freshMap = repository.findByKeys(queryKeys);
		if (freshMap.isEmpty()) {
			return map;
		}
		// 合并缓存结果和新查数据结果
		map.putAll(freshMap);
		// 保存新加入缓存的数据
		try {
			doSave(freshMap);
		} catch (Exception e) {
			LOG.error(null, e);
		}
		// 返回
		return map;
	}

	@Override
	public Long incrbyIfExists(K key, long num) throws CacheException {
		String fullKey = makeFullKey(key);
		Long result = executor.incrbyIfExists(fullKey, num);
		if (INFO_ENABLED) {
			LOG.info("[Cache] incrbyIfExists <{}>: {}", fullKey, result);
		}
		return result;
	}

	@Override
	public Double incrbyIfExists(K key, double num) throws CacheException {
		String fullKey = makeFullKey(key);
		Double result = executor.incrbyIfExists(fullKey, num);
		if (INFO_ENABLED) {
			LOG.info("[Cache] incrbyIfExists <{}>: {}", fullKey, result);
		}
		return result;
	}

	@Override
	public void save(K key, T value) {
		try {
			doSave(key, value);
		} catch (Exception ex) {
			onUpdateError(ex, key);
		}
	}

	@Override
	public void tryToSave(K key, T value) throws CacheException {
		doSave(key, value);
	}

	@Override
	public void saveMany(Map<K, T> items) {
		try {
			doSave(items);
		} catch (Exception ex) {
			onUpdateError(ex, items.keySet());
		}
	}

	@Override
	public void tryToSaveMany(Map<K, T> items) throws CacheException {
		doSave(items);
	}

	@Override
	public void remove(K key) {
		try {
			doRemove(key);
			// 防止大并发情况下，删除缓存操作时，另一个线程（或者进程）获取到脏数据（老数据），并把脏数据保存到缓存里
			schedule(new RemoveTask(key, 0), CONCURRENT_REFRESH_DELAY);
		} catch (Exception ex) {
			onUpdateError(ex, key);
		}
	}

	@Override
	public void tryToRemove(K key) throws CacheException {
		doRemove(key);
	}

	@Override
	public void removeMany(Collection<K> keys) {
		try {
			doRemove(keys);
			// 防止大并发情况下，删除缓存操作时，另一个线程（或者进程）获取到脏数据（老数据），并把脏数据保存到缓存里
			schedule(new RemoveManyTask(keys, 0), CONCURRENT_REFRESH_DELAY);
		} catch (Exception ex) {
			onUpdateError(ex, keys);
		}
	}

	@Override
	public void tryToRemoveMany(Collection<K> keys) throws CacheException {
		doRemove(keys);
	}

	@Override
	public void refresh(K key) {
		try {
			doRefresh(key);
		} catch (Exception ex) {
			onUpdateError(ex, key);
		}
	}

	@Override
	public T tryToRefresh(K key) throws CacheException {
		return doRefresh(key);
	}

	private void onUpdateError(Exception ex, K key) {
		LOG.error(null, ex);
		schedule(new RemoveTask(key, 0), ERROR_RETRY_DELAY);
	}

	private void onUpdateError(Exception ex, Collection<K> keys) {
		LOG.error(null, ex);
		schedule(new RemoveManyTask(keys, 0), ERROR_RETRY_DELAY);
	}

	private void schedule(Runnable task, int delay) {
		scheduledTaskExecutor.schedule(task, delay);
	}

	private class RemoveTask implements Runnable {
		K key;
		int retry;

		public RemoveTask(K key, int retry) {
			super();
			this.key = key;
			this.retry = retry;
		}

		@Override
		public void run() {
			try {
				doRemove(key);
			} catch (Exception e) {
				LOG.error(null, e);
				retry++;
				if (retry < 20) {
					schedule(new RemoveTask(key, retry), 8000);
				} else {
					LOG.error("[CacheWrap] Failed to remove: {}", key);
				}
			}
		}
	}

	private class RemoveManyTask implements Runnable {
		Collection<K> keys;
		int retry;

		public RemoveManyTask(Collection<K> keys, int retry) {
			super();
			this.keys = keys;
			this.retry = retry;
		}

		@Override
		public void run() {
			try {
				doRemove(keys);
			} catch (Exception e) {
				LOG.error(null, e);
				retry++;
				if (retry < 20) {
					schedule(new RemoveManyTask(keys, retry), 8000);
				} else {
					LOG.error("[CacheWrap] Failed to remove many: {}", keys);
				}
			}
		}
	}

	protected String makeFullKey(K key) {
		String prefix = policy.getPrefix();
		if (prefix == null || prefix.isEmpty()) {
			return key.toString();
		}
		return prefix + key.toString();
	}

	private T doFind(K key) throws CacheException {
		String fullKey = makeFullKey(key);
		byte[] value = executor.find(fullKey, policy);
		if (INFO_ENABLED) {
			LOG.info("[Cache] find <{}> <{}>", fullKey, value != null ? "cached" : "not found");
		}
		if (value == null) {
			return null;
		}
		return converter.deserialize(value);
	}

	private Map<K, T> doFind(Collection<K> keys) throws CacheException {
		List<String> fullKeys = keys.stream().map(this::makeFullKey).collect(Collectors.toList());
		Map<String, byte[]> bkv = executor.findMany(fullKeys, policy);
		List<String> foundKeys = INFO_ENABLED ? new ArrayList<>(bkv.size()) : null;
		int i = -1;
		Map<K, T> kv = new HashMap<>();
		for (K key : keys) {
			i++;
			String fullKey = fullKeys.get(i);
			byte[] bvalue = bkv.get(fullKey);
			if (bvalue == null) {
				continue;
			}
			T value = converter.deserialize(bvalue);
			kv.put(key, value);
			if (foundKeys != null) {
				foundKeys.add(fullKey);
			}
		}
		if (INFO_ENABLED) {
			LOG.info("[Cache] find <{}>, found: <{}>", fullKeys, foundKeys);
		}
		return kv;
	}

	private T doRefresh(K key) throws CacheException {
		// 从存储层中查找
		T value = repository.findByKey(key);
		if (value == null) {
			return null;
		}
		// 保存到缓存
		doSave(key, value);
		return value;
	}

	private void doSave(K key, T value) throws CacheException {
		if (saveFilter == null || saveFilter.filter(key, value)) {
			String fullKey = makeFullKey(key);
			executor.save(fullKey, doSerialize(value), policy);
			if (INFO_ENABLED) {
				LOG.info("[Cache] save <{}>", fullKey);
			}
		}
	}

	private void doSave(Map<K, T> items) throws CacheException {
		Map<String, byte[]> dataMap = new HashMap<>();
		for (Entry<K, T> entry : items.entrySet()) {
			K key = entry.getKey();
			T value = entry.getValue();
			if (saveFilter == null || saveFilter.filter(key, value)) {
				String fullKey = makeFullKey(key);
				dataMap.put(fullKey, doSerialize(value));
			}
		}
		if (dataMap.isEmpty()) {
			return;
		}
		executor.saveMany(dataMap, policy);
		if (INFO_ENABLED) {
			LOG.info("[Cache] saveMany <{}>", dataMap.keySet());
		}
	}

	private void doRemove(K key) throws CacheException {
		String fullKey = makeFullKey(key);
		if (INFO_ENABLED) {
			LOG.info("[Cache] remove <{}>", fullKey);
		}
		executor.remove(fullKey);
	}

	private void doRemove(Collection<K> keys) throws CacheException {
		List<String> fullKeys = keys.stream().map(this::makeFullKey).collect(Collectors.toList());
		if (INFO_ENABLED) {
			LOG.info("[Cache] removeMany <{}>", fullKeys);
		}
		executor.removeMany(fullKeys);
	}

	private byte[] doSerialize(T value) throws CacheException {
		byte[] data = converter.serialize(value);
		if (data == null) {
			throw new CacheException("[Cache] could not serialize to null");
		}
		return data;
	}

}
