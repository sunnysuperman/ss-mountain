package com.sunnysuperman.mountain.cache.converter;

import com.sunnysuperman.mountain.cache.CacheException;

@SuppressWarnings("squid:S6548")
public class ShortConverter implements Converter<Short> {
	private static final ShortConverter INSTANCE = new ShortConverter();

	public static final ShortConverter getInstance() {
		return INSTANCE;
	}

	private ShortConverter() {

	}

	@Override
	public Short deserialize(byte[] value) throws CacheException {
		return Short.parseShort(Encoder.encode(value));
	}

	@Override
	public byte[] serialize(Short model) throws CacheException {
		return Encoder.encode(model.toString());
	}
}
