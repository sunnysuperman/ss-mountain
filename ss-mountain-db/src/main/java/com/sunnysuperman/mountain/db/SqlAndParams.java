package com.sunnysuperman.mountain.db;

public class SqlAndParams {
	private String sql;
	private String countSql;
	private Object[] params;

	public SqlAndParams(String sql, Object[] params) {
		super();
		this.sql = sql;
		this.params = params;
	}

	public SqlAndParams(String sql, String countSql, Object[] params) {
		super();
		this.sql = sql;
		this.countSql = countSql;
		this.params = params;
	}

	public String getSql() {
		return sql;
	}

	public String getCountSql() {
		return countSql;
	}

	public Object[] getParams() {
		return params;
	}
}
