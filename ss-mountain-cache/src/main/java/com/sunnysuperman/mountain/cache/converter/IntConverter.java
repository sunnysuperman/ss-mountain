package com.sunnysuperman.mountain.cache.converter;

import com.sunnysuperman.mountain.cache.CacheException;

@SuppressWarnings("squid:S6548")
public class IntConverter implements Converter<Integer> {
	private static final IntConverter INSTANCE = new IntConverter();

	public static final IntConverter getInstance() {
		return INSTANCE;
	}

	private IntConverter() {

	}

	@Override
	public Integer deserialize(byte[] value) throws CacheException {
		return Integer.parseInt(Encoder.encode(value));
	}

	@Override
	public byte[] serialize(Integer model) throws CacheException {
		return Encoder.encode(model.toString());
	}
}
