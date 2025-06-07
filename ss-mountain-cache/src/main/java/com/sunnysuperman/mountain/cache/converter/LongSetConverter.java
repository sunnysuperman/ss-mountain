package com.sunnysuperman.mountain.cache.converter;

public class LongSetConverter extends SetConverter<Long> {

	private static final LongSetConverter INSTANCE = new LongSetConverter();

	public LongSetConverter() {
		super(Long.class);
	}

	public static LongSetConverter getInstance() {
		return INSTANCE;
	}

}
