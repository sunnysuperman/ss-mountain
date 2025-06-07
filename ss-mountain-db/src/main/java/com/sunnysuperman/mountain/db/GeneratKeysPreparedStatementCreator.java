package com.sunnysuperman.mountain.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.core.PreparedStatementCreator;

public class GeneratKeysPreparedStatementCreator implements PreparedStatementCreator {
	private String sql;

	public GeneratKeysPreparedStatementCreator(String sql) {
		super();
		this.sql = sql;
	}

	public String getSql() {
		return sql;
	}

	@Override
	public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
		return con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	}

}
