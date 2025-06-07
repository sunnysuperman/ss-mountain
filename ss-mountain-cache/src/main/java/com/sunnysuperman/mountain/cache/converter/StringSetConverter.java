package com.sunnysuperman.mountain.cache.converter;

public class StringSetConverter extends SetConverter<String> {

	private static final StringSetConverter INSTANCE = new StringSetConverter();

	public StringSetConverter() {
		super(String.class);
	}

	public static StringSetConverter getInstance() {
		return INSTANCE;
	}

}
