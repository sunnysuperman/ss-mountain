package com.sunnysuperman.mountain.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.support.JdbcUtils;

import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.repository.RepositoryException;

public class InsertPreparedStatementCallback<T> implements PreparedStatementCallback<T> {
	private Object[] params;
	private Class<T> generatedKeyClass;

	public InsertPreparedStatementCallback(Object[] params, Class<T> generatedKeyClass) {
		super();
		this.params = params;
		this.generatedKeyClass = generatedKeyClass;
	}

	@Override
	public T doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
		for (int i = 0; i < params.length; i++) {
			ps.setObject(i + 1, params[i]);
		}
		ps.executeUpdate();

		ResultSet rs = ps.getGeneratedKeys();
		if (rs == null) {
			return null;
		}
		try {
			if (rs.next()) {
				return convertGeneratedKey(rs.getObject(1), generatedKeyClass);
			}
			return null;
		} finally {
			JdbcUtils.closeResultSet(rs);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T convertGeneratedKey(Object k, Class<T> generatedKeyClass) {
		if (k == null) {
			return null;
		}
		Number convertedKey;
		if (generatedKeyClass == Long.class) {
			convertedKey = Num.parseLong(k);
		} else if (generatedKeyClass == Integer.class) {
			convertedKey = Num.parseInteger(k);
		} else if (generatedKeyClass == Number.class) {
			convertedKey = Num.parseNumber(k);
		} else {
			throw new RepositoryException("Unknown generated key class: " + generatedKeyClass);
		}
		return (T) convertedKey;
	}

}
