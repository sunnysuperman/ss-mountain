package com.sunnysuperman.mountain.db;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.sunnysuperman.mountain.db.mapper.DBMapper;
import com.sunnysuperman.mountain.db.mapper.EntityMapper;
import com.sunnysuperman.mountain.db.mapper.ObjectDBMapper;
import com.sunnysuperman.mountain.lang.pagination.Page;
import com.sunnysuperman.mountain.lang.pagination.PageRequest;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.repository.CRUDRepository;
import com.sunnysuperman.mountain.repository.RepositoryException;
import com.sunnysuperman.mountain.repository.SaveResult;
import com.sunnysuperman.mountain.repository.StaleEntityRepositoryException;
import com.sunnysuperman.mountain.repository.annotation.IdStrategy;

public abstract class DBCRUDRepository<T, I> extends DBRepository implements CRUDRepository<T, I> {
	private Class<T> entityClass;
	private DBMapper<T> entityMapper;
	private EntityMeta entityMeta;
	private Map<String, String> fieldColumnMapping = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	protected Class<T> getEntityClass() {
		if (entityClass == null) {
			entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		}
		return entityClass;
	}

	protected final DBMapper<T> getEntityMapper() {
		if (entityMapper == null) {
			entityMapper = new EntityMapper<>(getEntityClass(), getDefaultFieldConverter());
		}
		return entityMapper;
	}

	protected final <M> DBMapper<M> getEntityMapper(Class<M> clazz) {
		return new EntityMapper<>(clazz, getDefaultFieldConverter());
	}

	protected final String getTable() {
		return getEntityMeta().getTableName();
	}

	protected final String getColumnsByFields(String fields) {
		if (fields == null) {
			throw new IllegalArgumentException("fields");
		}
		String columns = fieldColumnMapping.get(fields);
		if (columns == null) {
			Set<String> fieldSet = new HashSet<>(Str.split(fields, ","));
			Set<String> columnSet = getEntityMeta().getColumnNames(fieldSet);
			if (columnSet.size() < fieldSet.size()) {
				throw new RepositoryException("Some columns not found for " + Str.join(fields));
			}
			columns = Str.join(columnSet);
			fieldColumnMapping.put(fields, columns);
		}
		return columns;
	}

	@SuppressWarnings("unchecked")
	protected final Map<I, T> list2map(List<T> list) {
		if (list.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<I, T> map = new HashMap<>();
		for (T item : list) {
			map.put((I) getEntityMeta().getEntityId(item), item);
		}
		return map;
	}

	protected DefaultFieldConverter getDefaultFieldConverter() {
		return BuiltInDefautFieldConverter.getInstance();
	}

	@Override
	public SaveResult save(T entity) throws RepositoryException {
		EntityMeta meta = getEntityMeta();
		Object entityId = meta.getEntityId(entity);
		if (entityId == null) {
			insert(entity);
			return SaveResult.RES_INSERTED;
		}
		boolean upsert = meta.getIdInfo().strategy() == IdStrategy.PROVIDED;
		if (upsert) {
			if (doUpdate(Collections.singletonList(entity), null, true)) {
				return SaveResult.RES_UPDATED;
			}
			insert(entity);
			return SaveResult.RES_INSERTED;
		}
		if (doUpdate(Collections.singletonList(entity), null, false)) {
			return SaveResult.RES_UPDATED;
		}
		return SaveResult.RES_NONE;
	}

	@Override
	public void insert(T entity) throws RepositoryException {
		doInsert(entity);
	}

	@Override
	public void insertBatch(List<T> entityList) throws RepositoryException {
		if (entityList.size() <= 1) {
			doInsert(entityList.get(0));
			return;
		}
		EntityMeta meta = getEntityMeta();
		DefaultFieldConverter converter = getDefaultFieldConverter();
		String sql = meta.getInsertSql(entityList.get(0), converter);
		SaveParams insertParams = meta.getInsertParams(entityList, converter);
		Class<?> generatedIdClass = meta.findGeneratedIdFieldType();
		if (generatedIdClass == null) {
			executeBatch(sql, insertParams.getParams());
		} else {
			List<?> generatedIds = getJdbcTemplate().execute(new GeneratKeysPreparedStatementCreator(sql),
					new InsertBatchPreparedStatementCallback<>(insertParams.getParams(), generatedIdClass));
			if (generatedIds == null || generatedIds.isEmpty()) {
				throw new RepositoryException("No id generated");
			}
			// 生成ID回传
			for (int i = 0; i < entityList.size(); i++) {
				meta.setEntityId(entityList.get(i), generatedIds.get(i), converter);
			}
		}
		// 版本号回传
		if (insertParams.versioning()) {
			for (int i = 0; i < entityList.size(); i++) {
				meta.setVersionValue(entityList.get(i), insertParams.getNewVersions().get(i));
			}
		}
	}

	@Override
	public boolean update(T entity) throws RepositoryException {
		return doUpdate(Collections.singletonList(entity), null, false);
	}

	@Override
	public boolean update(T entity, Set<String> fields) throws RepositoryException {
		return doUpdate(Collections.singletonList(entity), fields, false);
	}

	@Override
	public boolean updateBatch(List<T> entityList) throws RepositoryException {
		return doUpdate(entityList, null, false);
	}

	@Override
	public boolean updateBatch(List<T> entityList, Set<String> fields) throws RepositoryException {
		return doUpdate(entityList, fields, false);
	}

	@Override
	public void compareAndUpdateVersion(T entity) throws RepositoryException {
		if (!doUpdate(Collections.singletonList(entity), Collections.emptySet(), false)) {
			throw new RepositoryException("compareAndUpdateVersion failed");
		}
	}

	@Override
	public boolean deleteById(I id) throws RepositoryException {
		return doDeleteById(id);
	}

	@Override
	public int deleteByIds(Collection<I> ids) throws RepositoryException {
		if (ids.size() == 1) {
			return doDeleteById(ids.iterator().next()) ? 1 : 0;
		}
		EntityMeta meta = getEntityMeta();
		SqlAndParams sqlAndParams = meta.getDeleteByIdSqlAndParams(ids);
		return execute(sqlAndParams.getSql(), sqlAndParams.getParams());
	}

	@Override
	public boolean delete(T entity) throws RepositoryException {
		return doDelete(entity);
	}

	@Override
	public boolean deleteBatch(List<T> entityList) throws RepositoryException {
		if (entityList.size() == 1) {
			return doDelete(entityList.get(0));
		}
		EntityMeta meta = getEntityMeta();
		SqlAndBatchParams sqlAndParams = meta.getDeleteByEntityListSqlAndParams(entityList);
		int[] result = executeBatch(sqlAndParams.getSql(), sqlAndParams.getParams());
		List<Object> badIds = null;
		for (int i = 0; i < result.length; i++) {
			int deletedRow = result[i];
			if (deletedRow != 1) {
				if (badIds == null) {
					badIds = new ArrayList<>(Math.min(result.length, 3));
				}
				badIds.add(meta.getEntityId(entityList.get(i)));
			}
		}
		boolean success = badIds == null;
		if (!success && meta.getVersionField() != null) {
			throw new StaleEntityRepositoryException("Failed to delete entity for " + entityList.get(0).getClass() + "/"
					+ badIds.stream().map(Object::toString).collect(Collectors.joining(","))
					+ ", maybe entity is stale");
		}
		return success;
	}

	@Override
	public boolean existsById(I id) throws RepositoryException {
		Objects.requireNonNull(id);
		return find(getEntityMeta().getExistsByIdSql(), new Object[] { id }, ObjectDBMapper.getInstance()) != null;
	}

	@Override
	public T findById(I id) throws RepositoryException {
		Objects.requireNonNull(id);
		return find(getEntityMeta().getFindByIdSql(), new Object[] { id }, getEntityMapper());
	}

	@Override
	public T getById(I id) throws RepositoryException {
		T entity = findById(id);
		if (entity == null) {
			throw new RepositoryException("No entity " + getEntityClass() + " found of " + id);
		}
		return entity;
	}

	@Override
	public List<T> findByIds(Collection<I> ids) throws RepositoryException {
		if (ids.size() == 1) {
			T entity = findById(ids.iterator().next());
			if (entity == null) {
				return Collections.emptyList();
			}
			return Collections.singletonList(entity);
		}
		return findForList(getEntityMeta().getFindByIdsSql(ids), ids.toArray(), 0, 0, getEntityMapper());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> findByIdsInOrder(Collection<I> ids) throws RepositoryException {
		List<T> list = findByIds(ids);
		if (list.isEmpty()) {
			return Collections.emptyList();
		}
		Map<I, T> map = new HashMap<>();
		for (T item : list) {
			map.put((I) getEntityMeta().getEntityId(item), item);
		}
		List<T> listInOrder = new ArrayList<>(list.size());
		for (I id : ids) {
			T item = map.get(id);
			if (item != null) {
				listInOrder.add(item);
			}
		}
		return listInOrder;
	}

	@Override
	public Map<I, T> findForMapByIds(Collection<I> ids) throws RepositoryException {
		List<T> list = findByIds(ids);
		return list2map(list);
	}

	@Override
	public List<T> findAll() {
		return findForList(getEntityMeta().getFindAllSql(), null, 0, 0, getEntityMapper());
	}

	protected final int updateDoc(String tableName, Map<String, Object> doc, String key, Object value,
			DefaultFieldConverter converter) {
		for (Entry<String, Object> entry : doc.entrySet()) {
			Object val = entry.getValue();
			if (val != null) {
				entry.setValue(converter.convertToColumn(val));
			}
		}
		return super.updateDoc(tableName, doc, key, value);
	}

	protected final T find(String sql, Object[] params) {
		return find(sql, params, getEntityMapper());
	}

	protected final Page<T> findForPage(String sql, Object[] params, PageRequest page) {
		return findForPage(sql, params, page, getEntityMapper());
	}

	protected final Page<T> findForPage(String sql, String countSql, Object[] params, PageRequest page) {
		return findForPage(sql, countSql, params, page.getOffset(), page.getLimit(), getEntityMapper());
	}

	protected final PullPage<T> findForPullPage(String sql, Object[] params, PullPageRequest page) {
		return findForPullPage(sql, params, page, getEntityMapper());
	}

	protected final PullPage<T> findForPullPageByColumn(String sql, Object[] params, String column,
			PullPageRequest page) {
		return findForPullPageByColumn(sql, params, column, page.getLimit(), getEntityMapper());
	}

	protected final List<T> findForList(String sql, Object[] params, int offset, int limit) {
		return findForList(sql, params, offset, limit, getEntityMapper());
	}

	protected final List<T> findForList(String sql, Object[] params) {
		return findForList(sql, params, 0, 0, getEntityMapper());
	}

	protected final Set<T> findForSet(String sql, Object[] params, int offset, int limit) {
		return findForSet(sql, params, offset, limit, getEntityMapper());
	}

	private void doInsert(T entity) {
		EntityMeta meta = getEntityMeta();
		DefaultFieldConverter converter = getDefaultFieldConverter();
		String sql = meta.getInsertSql(entity, converter);
		SaveParams insertParams = meta.getInsertParams(Collections.singletonList(entity), converter);
		Class<?> generatedIdClass = meta.findGeneratedIdFieldType();
		if (generatedIdClass == null) {
			execute(sql, insertParams.getParams().get(0));
		} else {
			Object generatedId = getJdbcTemplate().execute(new GeneratKeysPreparedStatementCreator(sql),
					new InsertPreparedStatementCallback<>(insertParams.getParams().get(0), generatedIdClass));
			if (generatedId == null) {
				throw new RepositoryException("No id generated");
			}
			// 生成ID回传
			meta.setEntityId(entity, generatedId, converter);
		}
		// 版本号回传
		if (insertParams.versioning()) {
			meta.setVersionValue(entity, insertParams.getNewVersions().get(0));
		}
	}

	private boolean doUpdate(List<T> entityList, Set<String> fields, boolean upsert) {
		Object entity = entityList.get(0);
		EntityMeta meta = getEntityMeta();
		DefaultFieldConverter converter = getDefaultFieldConverter();
		String sql = meta.getUpdateSql(entity, fields, converter);
		SaveParams params = meta.getUpdateParams(entityList, fields, converter);
		int[] result;
		if (params.getParams().size() > 1) {
			result = executeBatch(sql, params.getParams());
		} else {
			result = new int[] { execute(sql, params.getParams().get(0)) };
		}
		boolean success = Arrays.stream(result).allMatch(i -> i > 0);
		boolean versioning = params.versioning();
		// 如果未能保存，如果版本控制，需要抛出相关异常，否则返回false
		if (!success) {
			if (versioning && !upsert) {
				throw versioningError(entityList, result);
			}
			return false;
		}
		// 保存成功，如果版本控制，版本号需要更新到实体
		if (versioning) {
			for (int i = 0; i < entityList.size(); i++) {
				meta.setVersionValue(entityList.get(i), params.getNewVersions().get(i));
			}
		}
		return true;
	}

	private StaleEntityRepositoryException versioningError(List<T> entityList, int[] result) {
		List<Object> badIds = new ArrayList<>(entityList.size());
		for (int i = 0; i < result.length; i++) {
			if (result[i] <= 0) {
				badIds.add(getEntityMeta().getEntityId(entityList.get(i)));
			}
		}
		return new StaleEntityRepositoryException(
				"Failed to update entity, maybe entity is stale: " + Str.join(badIds));
	}

	private EntityMeta getEntityMeta() {
		if (entityMeta == null) {
			entityMeta = EntityManager.getEntityMetaOf(getEntityClass());
		}
		return entityMeta;
	}

	private boolean doDeleteById(I id) throws RepositoryException {
		EntityMeta meta = getEntityMeta();
		String sql = meta.getDeleteByIdSql();
		return execute(sql, new Object[] { id }) > 0;
	}

	private boolean doDelete(T entity) throws RepositoryException {
		EntityMeta meta = getEntityMeta();
		SqlAndParams sqlAndParams = meta.getDeleteByEntitySqlAndParams(entity);
		boolean updated = execute(sqlAndParams.getSql(), sqlAndParams.getParams()) > 0;
		if (!updated && meta.getVersionField() != null) {
			throw new StaleEntityRepositoryException("Failed to delete entity for " + entity.getClass() + "/"
					+ meta.getEntityId(entity) + ", maybe entity is stale");
		}
		return updated;
	}

}
