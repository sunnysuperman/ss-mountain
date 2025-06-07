package com.sunnysuperman.mountain.db.mapper;

import java.util.Map;

@SuppressWarnings("squid:S6548")
public class MapDBMapper implements DBMapper<Map<String, Object>> {
	private MapDBMapper() {
	}

	@Override
	public Map<String, Object> map(Map<String, Object> row) {
		return row;
	}

	private static final MapDBMapper INSTANCE = new MapDBMapper();

	public static MapDBMapper getInstance() {
		return INSTANCE;
	}

}
