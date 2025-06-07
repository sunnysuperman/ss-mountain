package com.sunnysuperman.mountain.db;

import java.util.Map;

public class MultiColumn {
	private Map<String, Object> columns;

	public MultiColumn(Map<String, Object> columns) {
		super();
		this.columns = columns;
	}

	public Map<String, Object> getColumns() {
		return columns;
	}

}
