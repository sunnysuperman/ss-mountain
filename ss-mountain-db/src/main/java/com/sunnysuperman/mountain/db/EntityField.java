package com.sunnysuperman.mountain.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lang.utils.Types;
import com.sunnysuperman.mountain.repository.RepositoryException;
import com.sunnysuperman.mountain.repository.annotation.Column;
import com.sunnysuperman.mountain.repository.annotation.Entity;
import com.sunnysuperman.mountain.repository.annotation.ManyToOne;
import com.sunnysuperman.mountain.repository.annotation.OneToOne;
import com.sunnysuperman.mountain.repository.annotation.Table;
import com.sunnysuperman.mountain.repository.annotation.VersionControl;

class EntityField {
	private static final Long VERSION_LONG_1 = 1L;
	private static final Integer VERSION_INT_1 = 1;

	protected String fieldName;
	protected String columnName;
	protected Field field;
	protected Method readMethod;
	protected Method writeMethod;
	protected Column column;
	protected boolean relation;
	protected String relationFieldName;
	protected EntityField relationField;
	@SuppressWarnings("rawtypes")
	protected FieldConverter cachedConverter;

	static EntityField of(Field field, Method readMethod, Column column, Table table, Set<String> columnNames) {
		String columnName = column.name();
		if (columnName == null || columnName.isEmpty()) {
			columnName = field.getName();
		}
		columnName = table.mapCamelToUnderscore() ? Str.camel2Underline(columnName) : columnName;
		if (columnNames.contains(columnName)) {
			throw new RepositoryException("Duplicate column '" + columnName + "' in " + table.name());
		}
		columnNames.add(columnName);

		EntityField f = new EntityField();
		f.field = field;
		f.readMethod = readMethod;
		f.writeMethod = BeanUtils.getWriteMethodByField(field);
		f.fieldName = field.getName();
		f.columnName = columnName;
		f.column = column;
		OneToOne oneToOne = field.getAnnotation(OneToOne.class);
		ManyToOne manyToOne = oneToOne == null ? field.getAnnotation(ManyToOne.class) : null;
		if (oneToOne != null || manyToOne != null) {
			Class<?> relationType = field.getType();
			if (relationType.getAnnotation(Entity.class) == null) {
				throw new RepositoryException(
						"Field '" + field + "' is annotated with @OneToOne or @ManyToOne, but field type '"
								+ relationType + "' is not annotated with @Entity");
			}
			f.relation = true;
			f.relationFieldName = Str
					.emptyToNull(oneToOne != null ? oneToOne.relationField() : manyToOne.relationField());
		}
		return f;
	}

	boolean isValidVersionField() {
		if (field.getAnnotation(VersionControl.class) == null) {
			return false;
		}
		if (field.getType() != Integer.class && field.getType() != Long.class) {
			throw new RepositoryException("Version field should be Integer or Long: " + field);
		}
		if (!column.insertable() || !column.updatable()) {
			throw new RepositoryException("Version field should be insertable/updatable: " + field);
		}
		if (getConverter() != null) {
			throw new RepositoryException("Version field should not have a converter: " + field);
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	FieldConverter getConverter() {
		if (cachedConverter != null) {
			return cachedConverter;
		}
		Class<?> converterClass = column.converter();
		if (converterClass == void.class) {
			return null;
		}
		FieldConverter converter = Types.newInstance(converterClass);
		if (column.converterCache()) {
			cachedConverter = converter;
		}
		return converter;
	}

	public String getColumnName() {
		return columnName;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getColumnValue(Object entity, SerializeContext context) {
		Object fieldValue = getFieldValue(entity);
		// 自定义转换器
		FieldConverter converter = getConverter();
		if (converter != null) {
			return converter.convertToColumn(fieldValue, context);
		}
		if (fieldValue == null) {
			return null;
		}
		// 默认转换器
		if (!relation) {
			if (context == null) {
				return fieldValue;
			}
			return context.getDefaultFieldConverter().convertToColumn(fieldValue);
		}
		// 关联对象
		return ensureRelationField().getColumnValue(fieldValue, context);
	}

	public Object getVersionValue(Object entity) {
		return getFieldValue(entity);
	}

	public Object initVersionValue() {
		Class<?> type = getFieldType();
		if (type == long.class || type == Long.class) {
			return VERSION_LONG_1;
		}
		if (type == int.class || type == Integer.class) {
			return VERSION_INT_1;
		}
		throw new RepositoryException("Failed to init version for " + type);
	}

	public Object getNextVersionValue(Object entity) {
		Object fieldValue = getVersionValue(entity);
		if (fieldValue == null) {
			throw new RepositoryException("No version of " + entity.getClass());
		}
		Class<?> type = fieldValue.getClass();
		if (type == long.class || type == Long.class) {
			return Long.valueOf(Num.parseLongValue(fieldValue, 0) + 1);
		}
		if (type == int.class || type == Integer.class) {
			return Integer.valueOf(Num.parseIntValue(fieldValue, 0) + 1);
		}
		throw new RepositoryException("Failed to make next version for " + type);
	}

	public Class<?> getFieldType() {
		if (!relation) {
			return field.getType();
		}
		return ensureRelationField().getFieldType();
	}

	public Object getFieldValue(Object entity) {
		try {
			return readMethod.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RepositoryException(e);
		}
	}

	public Object getRelationFieldValue(Object entity) {
		if (!relation) {
			return getFieldValue(entity);
		}
		return ensureRelationField().getRelationFieldValue(getFieldValue(entity));
	}

	public void setFieldValue(Object entity, Object value) {
		try {
			writeMethod.invoke(entity, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RepositoryException(e);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public Object setFieldValue(Object entity, Object value, DBDeserializeContext context,
			DefaultFieldConverter defaultFieldConverter) throws Exception {
		// 1.自定义转换器
		FieldConverter converter = getConverter();
		if (converter != null) {
			@SuppressWarnings("unchecked")
			Object convertedValue = converter.convertToField(value, field.getType(), context);
			writeMethod.invoke(entity, convertedValue);
			return convertedValue;
		}
		// 2.null
		if (value == null) {
			return null;
		}
		// 3.默认转换器
		Class<?> type = field.getType();
		if (!relation) {
			// 3.1非关联对象
			if (value.getClass() != type) {
				value = defaultFieldConverter.convertToField(value, type, field.getGenericType());
			}
			try {
				writeMethod.invoke(entity, value);
			} catch (Exception ex) {
				throw new RepositoryException("Failed to set value of " + field.getName() + " by value: ["
						+ value.getClass() + "] " + value.toString(), ex);
			}
			return value;
		}
		// 3.2关联对象
		Object relationEntity = Types.newInstance(type);
		writeMethod.invoke(entity, relationEntity);
		ensureRelationField().setFieldValue(relationEntity, value, context, defaultFieldConverter);
		return relationEntity;
	}

	private EntityField ensureRelationField() {
		if (relationField != null) {
			return relationField;
		}
		EntityMeta meta = EntityManager.getEntityMetaOf(field.getType());
		if (relationFieldName == null) {
			relationField = meta.getIdField();
		} else {
			for (EntityField f : meta.getNormalFields()) {
				if (relationFieldName.equals(f.fieldName)) {
					relationField = f;
					break;
				}
			}
		}
		if (relationField == null) {
			throw new RepositoryException(field + " is not related to a valid entity");
		}
		return relationField;
	}
}