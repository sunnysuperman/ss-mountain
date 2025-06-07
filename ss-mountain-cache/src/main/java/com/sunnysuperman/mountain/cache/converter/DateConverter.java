package com.sunnysuperman.mountain.cache.converter;

import java.util.Date;

import com.sunnysuperman.mountain.cache.CacheException;

@SuppressWarnings("squid:S6548")
public final class DateConverter implements Converter<Date> {

	private static final DateConverter INSTANCE = new DateConverter();

	private DateConverter() {
		// nope
	}

	public static DateConverter getInstance() {
		return INSTANCE;
	}

	@Override
	public Date deserialize(byte[] value) throws CacheException {
		return new Date(Long.parseLong(Encoder.encode(value)));
	}

	@Override
	public byte[] serialize(Date model) throws CacheException {
		return Encoder.encode(String.valueOf(model.getTime()));
	}

}
