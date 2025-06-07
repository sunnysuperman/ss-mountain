package com.sunnysuperman.mountain.db;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.repository.RepositoryException;

final class BeanUtils {

	private BeanUtils() {
	}

	public static Field getFieldByReadMethod(Method method) throws RepositoryException {
		if (Modifier.isStatic(method.getModifiers())) {
			return null;
		}
		Class<?>[] paramTypes = method.getParameterTypes();
		if (paramTypes.length != 0) {
			return null;
		}
		String methodName = method.getName();
		int offset = 0;
		if (methodName.startsWith("get")) {
			offset = 3;
		} else if (methodName.startsWith("is")) {
			offset = 2;
		} else {
			return null;
		}
		final String fieldName = getFieldNameByMethod(methodName, offset);
		if (fieldName == null || fieldName.equals("class")) {
			return null;
		}
		Field field = null;
		try {
			field = method.getDeclaringClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RepositoryException("Could not find field '" + fieldName + "' in " + method.getDeclaringClass());
		}
		return field;
	}

	public static Method getWriteMethodByField(Field field) throws RepositoryException {
		try {
			return field.getDeclaringClass().getMethod("set" + Str.capitalize(field.getName()), field.getType());
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RepositoryException(
					"Could not find write-method of " + field.getName() + "' in " + field.getDeclaringClass());
		}
	}

	private static String getFieldNameByMethod(String methodName, int offset) {
		if (methodName.length() <= offset) {
			return null;
		}
		String field = methodName.substring(offset);
		if (field.length() == 1) {
			return field.toLowerCase();
		}
		return Character.toLowerCase(field.charAt(0)) + field.substring(1);
	}

}
