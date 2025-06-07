package com.sunnysuperman.mountain.cache.converter;

import com.sunnysuperman.mountain.cache.CacheException;

@SuppressWarnings("squid:S6548")
public class DoubleConverter implements Converter<Double> {
	private static final DoubleConverter INSTANCE = new DoubleConverter();

	public static final DoubleConverter getInstance() {
		return INSTANCE;
	}

	private DoubleConverter() {

	}

	@Override
	public Double deserialize(byte[] value) throws CacheException {
		return Double.parseDouble(Encoder.encode(value));
	}

	@Override
	public byte[] serialize(Double model) throws CacheException {
		return Encoder.encode(model.toString());
	}
}
