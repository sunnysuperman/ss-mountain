package com.sunnysuperman.mountain.cache.converter;

public class LongListConverter extends ListConverter<Long> {

	private static final LongListConverter INSTANCE = new LongListConverter();

	public LongListConverter() {
		super(Long.class);
	}

	public static LongListConverter getInstance() {
		return INSTANCE;
	}

}
