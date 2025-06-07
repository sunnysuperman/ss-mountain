package com.sunnysuperman.mountain.db;

import java.util.List;

public class SqlAndBatchParams {
	private String sql;
	private List<Object[]> params;

	public SqlAndBatchParams(String sql, List<Object[]> params) {
		super();
		this.sql = sql;
		this.params = params;
	}

	public String getSql() {
		return sql;
	}

	public List<Object[]> getParams() {
		return params;
	}

}
