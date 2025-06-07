package com.sunnysuperman.mountain.db.mapper;

import java.util.Map;

import com.sunnysuperman.mountain.lang.utils.Num;

@SuppressWarnings("squid:S6548")
public class DoubleDBMapper implements DBMapper<Double> {
	private DoubleDBMapper() {
	}

	@Override
	public Double map(Map<String, Object> doc) {
		if (doc.isEmpty()) {
			return null;
		}
		return Num.parseDouble(doc.values().iterator().next());
	}

	private static final DoubleDBMapper INSTANCE = new DoubleDBMapper();

	public static final DoubleDBMapper getInstance() {
		return INSTANCE;
	}

}
