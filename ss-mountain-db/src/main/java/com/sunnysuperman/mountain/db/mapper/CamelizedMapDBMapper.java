package com.sunnysuperman.mountain.db.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sunnysuperman.mountain.lang.utils.Str;

@SuppressWarnings("squid:S6548")
public class CamelizedMapDBMapper implements DBMapper<Map<String, Object>> {
	private CamelizedMapDBMapper() {
	}

	@Override
	public Map<String, Object> map(Map<String, Object> row) {
		Map<String, Object> doc = new HashMap<>();
		for (Entry<String, Object> entry : row.entrySet()) {
			doc.put(Str.underline2Camel(entry.getKey()), entry.getValue());
		}
		return doc;
	}

	private static final CamelizedMapDBMapper INSTANCE = new CamelizedMapDBMapper();

	public static CamelizedMapDBMapper getInstance() {
		return INSTANCE;
	}

}
