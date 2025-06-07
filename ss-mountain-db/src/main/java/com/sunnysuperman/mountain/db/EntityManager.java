package com.sunnysuperman.mountain.db;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.utils.Types;
import com.sunnysuperman.mountain.repository.RepositoryException;
import com.sunnysuperman.mountain.repository.annotation.Entity;

public class EntityManager {
	private static final Logger LOG = LoggerFactory.getLogger(EntityManager.class);
	private static Map<Class<?>, EntityMeta> metaMap = new ConcurrentHashMap<>();

	protected EntityManager() {
		// nope
	}

	public static void scan(String packageName) {
		scan(new String[] { packageName });
	}

	public static void scan(String[] packageNames) {
		long t1 = System.nanoTime();
		Set<Class<?>> classes = Types.findTypesAnnotatedWith(packageNames, Entity.class);
		for (Class<?> clazz : classes) {
			loadEntityMeta(clazz);
		}
		if (LOG.isInfoEnabled()) {
			long t2 = System.nanoTime();
			LOG.info("Entity scanning for package {} took {}ms, {} entities found, {}", packageNames,
					TimeUnit.NANOSECONDS.toMillis(t2 - t1), classes.size(), classes);
		}
	}

	public static <T> T deserialize(Map<String, Object> doc, Class<T> type, DefaultFieldConverter defaultFieldConverter)
			throws RepositoryException {
		EntityMeta meta = getEntityMetaOf(type);
		DBDeserializeContext context = new DBDeserializeContext(doc, defaultFieldConverter);
		try {
			T entity = Types.newInstance(type);
			for (EntityField field : meta.getNormalFields()) {
				field.setFieldValue(entity, doc.get(field.columnName), context, defaultFieldConverter);
			}
			EntityField idField = meta.getIdField();
			if (idField != null) {
				idField.setFieldValue(entity, doc.get(idField.columnName), context, defaultFieldConverter);
			}
			return entity;
		} catch (RepositoryException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RepositoryException(ex);
		}
	}

	protected static <T> void copyNotUpdatableFields(T src, T dest) {
		Objects.requireNonNull(src, "src");
		Objects.requireNonNull(dest, "dest");
		Class<?> type = src.getClass();
		EntityMeta meta = getEntityMetaOf(type);
		// 复制ID到新对象
		EntityField idField = meta.getIdField();
		idField.setFieldValue(dest, idField.getFieldValue(src));
		// 复制"不可更新或版本控制属性"到新对象
		meta.getNormalFields().forEach(field -> {
			if (!field.column.updatable() || field == meta.getVersionField()) {
				field.setFieldValue(dest, field.getFieldValue(src));
			}
		});
	}

	protected static EntityMeta getEntityMetaOf(Class<?> clazz) {
		EntityMeta meta = metaMap.get(clazz);
		if (meta == null) {
			LOG.warn("Lazy load entity {}", clazz);
			try {
				meta = loadEntityMeta(clazz);
			} catch (RepositoryException e) {
				throw e;
			} catch (Exception e) {
				throw new RepositoryException(e);
			}
		}
		return meta;
	}

	private static EntityMeta loadEntityMeta(Class<?> clazz) {
		EntityMeta meta = EntityMeta.of(clazz);
		metaMap.put(clazz, meta);
		return meta;
	}

}
