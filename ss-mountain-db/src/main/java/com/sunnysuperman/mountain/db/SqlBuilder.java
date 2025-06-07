package com.sunnysuperman.mountain.db;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public class SqlBuilder {
	private StringBuilder buf;
	private List<Object> paramList;

	public SqlBuilder(StringBuilder sql) {
		super();
		this.buf = sql;
		this.paramList = new ArrayList<>();
	}

	public SqlBuilder(String sql, Object... params) {
		this(new StringBuilder(sql.length()));
		append(sql, params);
	}

	public SqlBuilder append(String sql, Object... params) {
		if (params == null) {
			buf.append(sql);
			return this;
		}
		// and column = ?
		// and column = #
		// and column in (#)
		int sqlLength = sql.length();
		int paramIndex = -1;
		for (int i = 0; i < sqlLength; i++) {
			char c = sql.charAt(i);
			if (c == '#') {
				buf.append(appendParam(requireParam(params, ++paramIndex)));
				continue;
			}
			if (c == '?') {
				paramList.add(requireParam(params, ++paramIndex));
			}
			buf.append(c);
		}
		if (paramIndex + 1 != params.length) {
			throw new UnexpectedException(
					String.format("require %d parameters, but %d actual parameters", paramIndex + 1, params.length));
		}
		return this;
	}

	public SqlBuilder append(String sql) {
		buf.append(sql);
		return this;
	}

	public Object[] params() {
		return paramList.toArray();
	}

	public String sql() {
		return buf.toString();
	}

	@Override
	public String toString() {
		return sql();
	}

	private Object requireParam(Object[] params, int index) {
		if (index >= params.length) {
			throw new UnexpectedException(String.format("missing parameter %d", index + 1));
		}
		return params[index];
	}

	private String appendParam(Object param) {
		if (param instanceof Collection || param.getClass().isArray()) {
			int size;
			if (param instanceof Collection) {
				Collection<?> collection = (Collection<?>) param;
				paramList.addAll(collection);
				size = collection.size();
			} else {
				size = Array.getLength(param);
				for (int i = 0; i < size; i++) {
					paramList.add(Array.get(param, i));
				}
			}
			StringBuilder placeholder = new StringBuilder(size * 2 - 1);
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					placeholder.append(",?");
				} else {
					placeholder.append('?');
				}
			}
			return placeholder.toString();
		} else {
			paramList.add(param);
			return "?";
		}
	}
}
