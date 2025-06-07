package com.sunnysuperman.mountain.cache.converter;

import java.util.List;

import com.sunnysuperman.mountain.cache.CacheException;
import com.sunnysuperman.mountain.lang.utils.Jsons;

public class ListConverter<T> implements Converter<List<T>> {
	private Class<T> type;

	public ListConverter(Class<T> type) {
		super();
		this.type = type;
	}

	@Override
	public List<T> deserialize(byte[] bytes) throws CacheException {
		return Jsons.readForList(bytes, type);
	}

	@Override
	public byte[] serialize(List<T> obj) throws CacheException {
		return Jsons.writeAsBytes(obj);
	}
}
