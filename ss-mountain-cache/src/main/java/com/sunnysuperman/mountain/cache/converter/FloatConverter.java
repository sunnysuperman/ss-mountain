package com.sunnysuperman.mountain.cache.converter;

import com.sunnysuperman.mountain.cache.CacheException;

@SuppressWarnings("squid:S6548")
public class FloatConverter implements Converter<Float> {
	private static final FloatConverter INSTANCE = new FloatConverter();

	public static final FloatConverter getInstance() {
		return INSTANCE;
	}

	private FloatConverter() {

	}

	@Override
	public Float deserialize(byte[] value) throws CacheException {
		return Float.parseFloat(Encoder.encode(value));
	}

	@Override
	public byte[] serialize(Float model) throws CacheException {
		return Encoder.encode(model.toString());
	}
}
