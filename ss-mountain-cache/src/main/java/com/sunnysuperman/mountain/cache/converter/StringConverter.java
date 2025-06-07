package com.sunnysuperman.mountain.cache.converter;

import java.nio.charset.StandardCharsets;

import com.sunnysuperman.mountain.cache.CacheException;

@SuppressWarnings("squid:S6548")
public class StringConverter implements Converter<String> {
	private static final StringConverter INSTANCE = new StringConverter();

	public static final StringConverter getInstance() {
		return INSTANCE;
	}

	private StringConverter() {

	}

	@Override
	public String deserialize(byte[] value) throws CacheException {
		return new String(value, StandardCharsets.UTF_8);
	}

	@Override
	public byte[] serialize(String model) throws CacheException {
		return model.getBytes(StandardCharsets.UTF_8);
	}
}
