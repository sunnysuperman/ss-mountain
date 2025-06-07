package com.sunnysuperman.mountain.db.mapper;

import java.math.BigDecimal;
import java.util.Map;

import com.sunnysuperman.mountain.lang.utils.Num;

@SuppressWarnings("squid:S6548")
public class DecimalDBMapper implements DBMapper<BigDecimal> {
	private DecimalDBMapper() {
	}

	@Override
	public BigDecimal map(Map<String, Object> doc) {
		if (doc.isEmpty()) {
			return null;
		}
		return Num.parseDecimal(doc.values().iterator().next());
	}

	private static final DecimalDBMapper INSTANCE = new DecimalDBMapper();

	public static final DecimalDBMapper getInstance() {
		return INSTANCE;
	}

}
