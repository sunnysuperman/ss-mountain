package com.sunnysuperman.mountain.cache.converter;

import com.sunnysuperman.mountain.cache.CacheException;

@SuppressWarnings("squid:S6548")
public class ByteConverter implements Converter<Byte> {
	private static final ByteConverter INSTANCE = new ByteConverter();

	public static final ByteConverter getInstance() {
		return INSTANCE;
	}

	private ByteConverter() {

	}

	@Override
	public Byte deserialize(byte[] value) throws CacheException {
		return value[0];
	}

	@Override
	public byte[] serialize(Byte model) throws CacheException {
		return new byte[] { model };
	}
}
