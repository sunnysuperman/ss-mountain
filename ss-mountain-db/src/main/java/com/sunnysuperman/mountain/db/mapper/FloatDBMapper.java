package com.sunnysuperman.mountain.db.mapper;

import java.util.Map;

import com.sunnysuperman.mountain.lang.utils.Num;

@SuppressWarnings("squid:S6548")
public class FloatDBMapper implements DBMapper<Double> {
	private FloatDBMapper() {
	}

	@Override
	public Double map(Map<String, Object> doc) {
		if (doc.isEmpty()) {
			return null;
		}
		return Num.parseDouble(doc.values().iterator().next());
	}

	private static final FloatDBMapper INSTANCE = new FloatDBMapper();

	public static final FloatDBMapper getInstance() {
		return INSTANCE;
	}

}
