package com.sunnysuperman.mountain.cache.converter;

import com.sunnysuperman.mountain.cache.CacheException;

@SuppressWarnings("squid:S6548")
public class LongConverter implements Converter<Long> {
	private static final LongConverter INSTANCE = new LongConverter();

	public static final LongConverter getInstance() {
		return INSTANCE;
	}

	private LongConverter() {

	}

	@Override
	public Long deserialize(byte[] value) throws CacheException {
		return Long.parseLong(Encoder.encode(value));
	}

	@Override
	public byte[] serialize(Long model) throws CacheException {
		return Encoder.encode(model.toString());
	}
}
