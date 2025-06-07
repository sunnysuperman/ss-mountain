package com.sunnysuperman.mountain.cache.converter;

public class StringListConverter extends ListConverter<String> {

	private static final StringListConverter INSTANCE = new StringListConverter();

	public StringListConverter() {
		super(String.class);
	}

	public static StringListConverter getInstance() {
		return INSTANCE;
	}

}
