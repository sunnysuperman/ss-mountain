package com.sunnysuperman.mountain.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.jdbc.core.JdbcTemplate;

import com.sunnysuperman.mountain.db.mapper.DBMapper;
import com.sunnysuperman.mountain.db.mapper.ObjectDBMapper;
import com.sunnysuperman.mountain.lang.pagination.Page;
import com.sunnysuperman.mountain.lang.pagination.PageRequest;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.repository.RepositoryException;

public abstract class DBRepository {

	protected abstract JdbcTemplate getJdbcTemplate();

	protected String convertColumnName(String columnName) {
		return columnName;
	}

	protected SqlAndParams getInsertDialect(String tableName, Map<String, ?> doc) {
		StringBuilder buf = new StringBuilder("insert into ").append(tableName).append('(');
		StringBuilder buf2 = new StringBuilder();
		List<Object> params = new ArrayList<>(doc.size());

		int i = -1;
		for (Entry<String, ?> entry : doc.entrySet()) {
			i++;
			if (i > 0) {
				buf.append(',');
				buf2.append(',');
			}
			buf.append(convertColumnName(entry.getKey()));
			Object fieldValue = entry.getValue();
			if (fieldValue instanceof DBFunction) {
				DBFunction cf = (DBFunction) fieldValue;
				buf2.append(cf.getFunction());
				if (cf.getParams() != null) {
					params.addAll(cf.getParams());
				}
			} else {
				buf2.append('?');
				params.add(fieldValue);
			}
		}

		buf.append(") values(");
		buf.append(buf2);
		buf.append(')');

		return new SqlAndParams(buf.toString(), params.toArray(new Object[params.size()]));
	}

	@SuppressWarnings("squid:S3776")
	protected SqlAndParams getUpdateDialect(String tableName, Map<String, ?> doc, String[] filterColumns,
			Object[] filterValues) {
		StringBuilder sql = new StringBuilder("update ");
		sql.append(tableName);
		sql.append(" set ");
		List<Object> params = new LinkedList<>();
		int offset = 0;
		for (Entry<String, ?> entry : doc.entrySet()) {
			String fieldKey = entry.getKey();
			Object fieldValue = entry.getValue();
			if (fieldKey.indexOf('$') == 0) {
				if (fieldKey.equals("$inc")) {
					Map<?, ?> fieldValueAsMap = (Map<?, ?>) fieldValue;
					for (Entry<?, ?> incEntry : fieldValueAsMap.entrySet()) {
						String incKey = convertColumnName(incEntry.getKey().toString());
						Number incValue = Num.parseNumber(incEntry.getValue());
						if (offset > 0) {
							sql.append(",");
						}
						offset++;
						sql.append(incKey).append("=").append(incKey).append("+?");
						params.add(incValue);
					}
				}
				continue;
			}
			fieldKey = convertColumnName(fieldKey);
			if (offset > 0) {
				sql.append(",");
			}
			offset++;
			sql.append(fieldKey);
			if (fieldValue instanceof DBFunction) {
				// column=ST_GeometryFromText(?,4326), column=point(?,?), etc.
				DBFunction func = (DBFunction) fieldValue;
				sql.append("=").append(func.getFunction());
				if (func.getParams() != null) {
					params.addAll(func.getParams());
				}
			} else {
				sql.append("=?");
				params.add(fieldValue);
			}
		}
		sql.append(" where ");
		for (int i = 0; i < filterColumns.length; i++) {
			if (i > 0) {
				sql.append(" and ");
			}
			String pk = filterColumns[i];
			sql.append(convertColumnName(pk));
			sql.append("=?");
			params.add(filterValues[i]);
		}
		Object[] paramsAsArray = params.toArray(new Object[params.size()]);
		return new SqlAndParams(sql.toString(), paramsAsArray);
	}

	protected int maxNumberOfRows() {
		return 10000;
	}

	protected String getPagingDialect(String sql, int offset, int limit) {
		if (limit <= 0) {
			limit = maxNumberOfRows();
			if (limit <= 0) {
				return sql;
			}
		}
		return new StringBuilder(sql).append(" limit ").append(offset).append(",").append(limit).toString();
	}

	protected String getCountDialect(String sql) {
		int index1 = sql.indexOf(" from ");
		if (index1 <= 0) {
			throw new RepositoryException("Bad sql: " + sql);
		}
		int index2 = sql.indexOf(" order by", index1);
		if (index2 < 0) {
			index2 = sql.length();
		}
		StringBuilder buf = new StringBuilder("select count(*)").append(sql.substring(index1, index2));
		return buf.toString();
	}

	protected String getExecuteLimitDialect(String sql, int limit) {
		return sql + " limit " + limit;
	}

	public int execute(String sql, Object[] params) {
		return getJdbcTemplate().update(sql, params);
	}

	public int execute(String sql, Object[] params, int limit) {
		return execute(getExecuteLimitDialect(sql, limit), params);
	}

	public int[] executeBatch(String sql, List<Object[]> batchParams) {
		return getJdbcTemplate().batchUpdate(sql, batchParams);
	}

	public boolean insertDoc(String tableName, Map<String, Object> doc) {
		SqlAndParams sp = getInsertDialect(tableName, doc);
		return execute(sp.getSql(), sp.getParams()) > 0;
	}

	public <T extends Number> T insertDoc(String tableName, Map<String, Object> doc, Class<T> generatedKeyClass) {
		SqlAndParams sp = getInsertDialect(tableName, doc);
		// 无需生成自增ID
		if (generatedKeyClass == null) {
			execute(sp.getSql(), sp.getParams());
			return null;
		}
		// insert并生成自增ID
		T generatedKey = getJdbcTemplate().execute(new GeneratKeysPreparedStatementCreator(sp.getSql()),
				new InsertPreparedStatementCallback<>(sp.getParams(), generatedKeyClass));
		if (generatedKey == null) {
			throw new RepositoryException("Insert error: no id generated");
		}
		return generatedKey;
	}

	public void insertDocs(String tableName, List<Map<String, Object>> docs) {
		insertDocs(tableName, docs, null);
	}

	public <T extends Number> List<T> insertDocs(String tableName, List<Map<String, Object>> docs,
			Class<T> generatedKeyClass) {
		// 单个插入
		if (docs.size() == 1) {
			T generatedKey = insertDoc(tableName, docs.get(0), generatedKeyClass);
			if (generatedKey == null) {
				return Collections.emptyList();
			}
			return Collections.singletonList(generatedKey);
		}
		Map<String, Object> testDoc = docs.get(0);
		String[] columns = new String[testDoc.size()];
		List<Object[]> paramsBatch = new ArrayList<>(docs.size());
		String sql = makeInsertSql(tableName, testDoc, columns);
		for (Map<String, Object> doc : docs) {
			Object[] params = new Object[columns.length];
			for (int i = 0; i < columns.length; i++) {
				params[i] = doc.get(columns[i]);
			}
			paramsBatch.add(params);
		}
		// 无需生成自增ID
		if (generatedKeyClass == null) {
			getJdbcTemplate().batchUpdate(sql, paramsBatch);
			return Collections.emptyList();
		}
		// insert并生成自增ID
		List<T> generatedKeys = getJdbcTemplate().execute(new GeneratKeysPreparedStatementCreator(sql),
				new InsertBatchPreparedStatementCallback<>(paramsBatch, generatedKeyClass));
		if (generatedKeys == null || generatedKeys.isEmpty()) {
			throw new RepositoryException("Insert error: no id generated");
		}
		return generatedKeys;
	}

	public int updateDoc(String tableName, Map<String, Object> doc, String[] keys, Object[] values) {
		SqlAndParams sp = getUpdateDialect(tableName, doc, keys, values);
		return execute(sp.getSql(), sp.getParams());
	}

	public int updateDoc(String tableName, Map<String, Object> doc, String key, Object value) {
		return updateDoc(tableName, doc, new String[] { key }, new Object[] { value });
	}

	public InsertUpdate saveDoc(String tableName, Map<String, Object> doc, String primaryKey) {
		Object primaryValue = doc.remove(primaryKey);
		if (primaryValue == null) {
			boolean inserted = insertDoc(tableName, doc);
			return inserted ? InsertUpdate.INSERT : null;
		}
		boolean updated = updateDoc(tableName, doc, primaryKey, primaryValue) > 0;
		if (updated) {
			return InsertUpdate.UPDATE;
		}
		doc.put(primaryKey, primaryValue);
		boolean inserted = insertDoc(tableName, doc);
		return inserted ? InsertUpdate.INSERT : null;
	}

	public InsertUpdate saveDoc(String tableName, Map<String, Object> doc) {
		return saveDoc(tableName, doc, "id");
	}

	public <T> T find(String sql, Object[] params, DBMapper<T> mapper) {
		List<Map<String, Object>> rawItems = getJdbcTemplate().queryForList(getPagingDialect(sql, 0, 1), params);
		if (rawItems.isEmpty()) {
			return null;
		}
		Map<String, Object> rawItem = rawItems.get(0);
		return mapper.map(rawItem);
	}

	public <T> List<T> findForList(String sql, Object[] params, int offset, int limit, DBMapper<T> mapper) {
		List<Map<String, Object>> rawItems = getJdbcTemplate().queryForList(getPagingDialect(sql, offset, limit),
				params);
		if (rawItems.isEmpty()) {
			return Collections.emptyList();
		}
		List<T> items = new ArrayList<>(rawItems.size());
		for (Map<String, Object> rawItem : rawItems) {
			T item = mapper.map(rawItem);
			items.add(item);
		}
		return items;
	}

	public <T> Set<T> findForSet(String sql, Object[] params, int offset, int limit, DBMapper<T> mapper) {
		List<Map<String, Object>> rawItems = getJdbcTemplate().queryForList(getPagingDialect(sql, offset, limit),
				params);
		if (rawItems.isEmpty()) {
			return Collections.emptySet();
		}
		Set<T> items = new HashSet<>(rawItems.size());
		for (Map<String, Object> rawItem : rawItems) {
			T item = mapper.map(rawItem);
			items.add(item);
		}
		return items;
	}

	public <T> Page<T> findForPage(String sql, Object[] params, int offset, int limit, DBMapper<T> mapper) {
		List<T> items = findForList(sql, params, offset, limit, mapper);
		int size = items.size();
		if (size == 0) {
			return Page.empty(limit);
		}
		if (offset != 0 || size == limit) {
			size = count(getCountDialect(sql), params);
		}
		return new Page<>(items, size, offset, limit);
	}

	public <T> Page<T> findForPage(String sql, Object[] params, PageRequest page, DBMapper<T> mapper) {
		return findForPage(sql, params, page.getOffset(), page.getLimit(), mapper);
	}

	public <T> Page<T> findForPage(String sql, String countSql, Object[] params, int offset, int limit,
			DBMapper<T> mapper) {
		List<T> items = findForList(sql, params, offset, limit, mapper);
		int size = items.size();
		if (size == 0) {
			return Page.empty(limit);
		}
		if (offset != 0 || size == limit) {
			size = count(countSql != null ? countSql : getCountDialect(sql), params);
		}
		return new Page<>(items, size, offset, limit);
	}

	public <T> PullPage<T> findForPullPage(String sql, Object[] params, String marker, int limit, DBMapper<T> mapper) {
		int offset = marker == null ? 0 : Integer.parseInt(marker);
		List<Map<String, Object>> rawItems = getJdbcTemplate().queryForList(getPagingDialect(sql, offset, limit + 1),
				params);
		if (rawItems.isEmpty()) {
			return PullPage.empty();
		}
		boolean hasMore = rawItems.size() > limit;
		List<T> items = new ArrayList<>(Math.min(limit, rawItems.size()));
		for (Map<String, Object> rawItem : rawItems) {
			T item = mapper.map(rawItem);
			items.add(item);
			if (items.size() >= limit) {
				break;
			}
		}
		int newOffset = offset + limit;
		return PullPage.of(items, String.valueOf(newOffset), hasMore);
	}

	public <T> PullPage<T> findForPullPage(String sql, Object[] params, PullPageRequest page, DBMapper<T> mapper) {
		return findForPullPage(sql, params, page.getMarker(), page.getLimit(), mapper);
	}

	public <T> PullPage<T> findForPullPageByColumn(String sql, Object[] params, String column, int limit,
			DBMapper<T> mapper) {
		List<Map<String, Object>> rawItems = getJdbcTemplate().queryForList(getPagingDialect(sql, 0, limit + 1),
				params);
		if (rawItems.isEmpty()) {
			return PullPage.empty();
		}
		List<T> items = new ArrayList<>(Math.min(limit, rawItems.size()));
		boolean hasMore = rawItems.size() > limit;
		String newMarker = null;
		if (hasMore) {
			newMarker = rawItems.get(limit - 1).get(column).toString();
		}
		for (Map<String, Object> rawItem : rawItems) {
			T item = mapper.map(rawItem);
			items.add(item);
			if (items.size() >= limit) {
				break;
			}
		}
		return PullPage.of(items, newMarker, hasMore);
	}

	public <T> PullPage<T> findForPullPageByColumn(String sql, Object[] params, String column, PullPageRequest page,
			DBMapper<T> mapper) {
		return findForPullPageByColumn(sql, params, column, page.getLimit(), mapper);
	}

	@SuppressWarnings("deprecation")
	public int count(String sql, Object[] params) {
		Integer val = getJdbcTemplate().queryForObject(sql, params, Integer.class);
		return Num.parseIntValue(val, 0);
	}

	public boolean exists(String sql, Object[] params) {
		return find(sql, params, ObjectDBMapper.getInstance()) != null;
	}

	private String makeInsertSql(String tableName, Map<String, Object> testDoc, String[] columns) {
		int i = 0;
		StringBuilder buf = new StringBuilder("insert into ");
		StringBuilder buf2 = new StringBuilder();
		buf.append(tableName);
		buf.append('(');
		for (Entry<String, Object> entry : testDoc.entrySet()) {
			String key = entry.getKey();
			columns[i] = key;
			if (i > 0) {
				buf.append(',');
				buf2.append(',');
			}
			buf.append(convertColumnName(key));
			buf2.append('?');
			i++;
		}

		buf.append(") values(");
		buf.append(buf2);
		buf.append(')');
		return buf.toString();
	}
}
