package com.sunnysuperman.mountain.cache.converter;

import com.sunnysuperman.mountain.cache.CacheException;
import com.sunnysuperman.mountain.lang.utils.Jsons;

public class ObjectConverter<T> implements Converter<T> {
	private Class<T> type;

	public ObjectConverter(Class<T> type) {
		super();
		this.type = type;
	}

	@Override
	public T deserialize(byte[] bytes) throws CacheException {
		return Jsons.read(bytes, type);
	}

	@Override
	public byte[] serialize(T obj) throws CacheException {
		return Jsons.writeAsBytes(obj);
	}
}
