package com.sunnysuperman.mountain.cache.converter;

import com.sunnysuperman.mountain.cache.CacheException;

public interface Converter<T> {

	T deserialize(byte[] value) throws CacheException;

	byte[] serialize(T model) throws CacheException;

}
