package com.sunnysuperman.mountain.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;

import com.sunnysuperman.mountain.lang.enums.CodeAwareEnum;
import com.sunnysuperman.mountain.lang.utils.Bool;
import com.sunnysuperman.mountain.lang.utils.Dates;
import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.repository.RepositoryException;

@SuppressWarnings({ "squid:S6548", "squid:S3776" })
public class BuiltInDefautFieldConverter implements DefaultFieldConverter {
	private static final BuiltInDefautFieldConverter INSTANCE = new BuiltInDefautFieldConverter();

	public static BuiltInDefautFieldConverter getInstance() {
		return INSTANCE;
	}

	@Override
	public Object convertToColumn(Object fieldValue) {
		Class<?> type = fieldValue.getClass();
		// 常见类型
		if (isSimpleType(type)) {
			return fieldValue;
		}
		if (fieldValue instanceof Date) {
			return ((Date) fieldValue).getTime();
		}
		if (fieldValue instanceof CodeAwareEnum) {
			return ((CodeAwareEnum) fieldValue).code();
		}
		if (fieldValue instanceof Enum) {
			return ((Enum<?>) fieldValue).name();
		}
		// 默认转换
		return convertToColumnByDefault(fieldValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convertToField(Object columnValue, Class<?> type, Type genericType) {
		// 常见类型
		if (type == String.class) {
			return Str.parse(columnValue);
		}
		if (type == Integer.class || type == int.class) {
			return Num.parseInteger(columnValue);
		}
		if (type == Long.class || type == long.class) {
			return Num.parseLong(columnValue);
		}
		if (type == Boolean.class || type == boolean.class) {
			return Bool.parse(columnValue);
		}
		if (type == Double.class || type == double.class) {
			return Num.parseDouble(columnValue);
		}
		if (type == Float.class || type == float.class) {
			return Num.parseFloat(columnValue);
		}
		if (type == Byte.class || type == byte.class) {
			return Num.parseByte(columnValue);
		}
		if (type == Short.class || type == short.class) {
			return Num.parseShort(columnValue);
		}
		if (type == BigDecimal.class) {
			return Num.parseDecimal(columnValue);
		}
		if (type == Date.class) {
			return Dates.parseDate(columnValue);
		}
		if (CodeAwareEnum.class.isAssignableFrom(type)) {
			return CodeAwareEnum.fromCode((Class<? extends CodeAwareEnum>) type,
					Num.parseByte(columnValue).byteValue());
		}
		if (Enum.class.isAssignableFrom(type)) {
			try {
				return type.getMethod("valueOf", String.class).invoke(null, columnValue.toString());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				throw new RepositoryException(e);
			}
		}
		return convertToFieldByDefault(columnValue, type, genericType);
	}

	protected Object convertToColumnByDefault(Object fieldValue) {
		return Jsons.write(fieldValue);
	}

	protected Object convertToFieldByDefault(Object columnValue, Class<?> type, Type genericType) {
		return Jsons.read(columnValue.toString(), type, genericType);
	}

	protected boolean isSimpleType(Class<?> type) {
		if (type.isPrimitive()) {
			return true;
		}
		if (type == String.class || type == Long.class || type == Integer.class || type == Double.class
				|| type == Float.class || type == Boolean.class || type == Byte.class || type == Short.class
				|| type == Character.class) {
			return true;
		}
		// 二进制
		return type.isArray() && type.getComponentType().equals(byte.class);
	}

}
