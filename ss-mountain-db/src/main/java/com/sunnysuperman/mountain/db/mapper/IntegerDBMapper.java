package com.sunnysuperman.mountain.db.mapper;

import java.util.Map;

import com.sunnysuperman.mountain.lang.utils.Num;

@SuppressWarnings("squid:S6548")
public class IntegerDBMapper implements DBMapper<Integer> {
	private IntegerDBMapper() {
	}

	@Override
	public Integer map(Map<String, Object> doc) {
		if (doc.isEmpty()) {
			return null;
		}
		return Num.parseInteger(doc.values().iterator().next());
	}

	private static final IntegerDBMapper INSTANCE = new IntegerDBMapper();

	public static final IntegerDBMapper getInstance() {
		return INSTANCE;
	}

}
