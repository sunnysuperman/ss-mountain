package com.sunnysuperman.mountain.cache;

public interface CacheSaveFilter<T, K> {

	boolean filter(K key, T value);

}
