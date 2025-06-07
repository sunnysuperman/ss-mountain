package com.sunnysuperman.mountain.db.mapper;

import java.util.Map;

import com.sunnysuperman.mountain.lang.utils.Str;

@SuppressWarnings("squid:S6548")
public class StringDBMapper implements DBMapper<String> {

	private StringDBMapper() {
	}

	@Override
	public String map(Map<String, Object> doc) {
		if (doc.isEmpty()) {
			return null;
		}
		return Str.parse(doc.values().iterator().next());
	}

	private static final StringDBMapper INSTANCE = new StringDBMapper();

	public static final StringDBMapper getInstance() {
		return INSTANCE;
	}

}
