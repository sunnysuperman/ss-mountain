package com.sunnysuperman.mountain.cache.provider;

import java.util.Collection;
import java.util.Map;

public interface RepositoryProvider<T, K> {

	T findByKey(K key);

	Map<K, T> findByKeys(Collection<K> keys);

}
