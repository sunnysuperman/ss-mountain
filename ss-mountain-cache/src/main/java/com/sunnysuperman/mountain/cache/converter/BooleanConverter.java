package com.sunnysuperman.mountain.cache.converter;

import com.sunnysuperman.mountain.cache.CacheException;

@SuppressWarnings("squid:S6548")
public class BooleanConverter implements Converter<Boolean> {
	private static final BooleanConverter INSTANCE = new BooleanConverter();

	public static final BooleanConverter getInstance() {
		return INSTANCE;
	}

	private BooleanConverter() {

	}

	@Override
	public Boolean deserialize(byte[] value) throws CacheException {
		return value[0] > 0;
	}

	@Override
	public byte[] serialize(Boolean model) throws CacheException {
		byte b = (byte) (model.booleanValue() ? 1 : 0);
		return new byte[] { b };
	}

}
