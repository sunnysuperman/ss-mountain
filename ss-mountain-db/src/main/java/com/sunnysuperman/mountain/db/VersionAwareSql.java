package com.sunnysuperman.mountain.db;

public class VersionAwareSql {
	private String sql;
	private boolean hasVesion;

	public VersionAwareSql(String sql, boolean hasVesion) {
		super();
		this.sql = sql;
		this.hasVesion = hasVesion;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public boolean isHasVesion() {
		return hasVesion;
	}

	public void setHasVesion(boolean hasVesion) {
		this.hasVesion = hasVesion;
	}

}
