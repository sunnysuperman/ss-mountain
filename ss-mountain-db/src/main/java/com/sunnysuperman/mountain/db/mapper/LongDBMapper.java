package com.sunnysuperman.mountain.db.mapper;

import java.util.Map;

import com.sunnysuperman.mountain.lang.utils.Num;

@SuppressWarnings("squid:S6548")
public class LongDBMapper implements DBMapper<Long> {
	private LongDBMapper() {
	}

	@Override
	public Long map(Map<String, Object> doc) {
		if (doc.isEmpty()) {
			return null;
		}
		return Num.parseLong(doc.values().iterator().next());
	}

	private static final LongDBMapper INSTANCE = new LongDBMapper();

	public static final LongDBMapper getInstance() {
		return INSTANCE;
	}

}
