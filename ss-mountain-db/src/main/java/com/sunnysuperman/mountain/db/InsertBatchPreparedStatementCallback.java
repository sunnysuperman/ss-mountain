package com.sunnysuperman.mountain.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.JdbcUtils;

public class InsertBatchPreparedStatementCallback<T> implements PreparedStatementCallback<List<T>> {
	private List<Object[]> paramsBatch;
	private Class<T> generatedKeyClass;

	public InsertBatchPreparedStatementCallback(List<Object[]> paramsBatch, Class<T> generatedKeyClass) {
		super();
		this.paramsBatch = paramsBatch;
		this.generatedKeyClass = generatedKeyClass;
	}

	@Override
	public List<T> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
		for (Object[] params : paramsBatch) {
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			ps.addBatch();
		}
		ps.executeBatch();

		ResultSet rs = ps.getGeneratedKeys();
		if (rs == null) {
			return Collections.emptyList();
		}
		try {
			List<T> results = new ArrayList<>(paramsBatch.size());
			while (rs.next()) {
				results.add(InsertPreparedStatementCallback.convertGeneratedKey(rs.getObject(1), generatedKeyClass));
			}
			return results;
		} finally {
			JdbcUtils.closeResultSet(rs);
		}
	}

}
