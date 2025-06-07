package com.sunnysuperman.mountain.cache.converter;

import java.util.HashSet;
import java.util.Set;

import com.sunnysuperman.mountain.cache.CacheException;
import com.sunnysuperman.mountain.lang.utils.Jsons;

public class SetConverter<T> implements Converter<Set<T>> {
	private Class<T> type;

	public SetConverter(Class<T> type) {
		super();
		this.type = type;
	}

	@Override
	public Set<T> deserialize(byte[] bytes) throws CacheException {
		return new HashSet<>(Jsons.readForList(bytes, type));
	}

	@Override
	public byte[] serialize(Set<T> obj) throws CacheException {
		return Jsons.writeAsBytes(obj);
	}
}
