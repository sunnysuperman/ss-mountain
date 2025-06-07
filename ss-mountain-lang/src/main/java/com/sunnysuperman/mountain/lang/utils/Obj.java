package com.sunnysuperman.mountain.lang.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public final class Obj {

	protected Obj() {
	}

	public static <T> T or(T obj, T defaults) {
		if (obj != null) {
			return obj;
		}
		return defaults;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getProperty(Object object, String propertyName) {
		try {
			return (T) getPropertyDescriptor(object, propertyName).getReadMethod().invoke(object);
		} catch (BeansException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new UnexpectedException(e);
		}
	}

	public static void setProperty(Object object, String propertyName, Object value) {
		try {
			getPropertyDescriptor(object, propertyName).getWriteMethod().invoke(object, value);
		} catch (BeansException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new UnexpectedException(e);
		}
	}

	public static void setPropertiesNull(Collection<?> objects, Set<String> nullProps) {
		if (objects.isEmpty()) {
			return;
		}
		Class<?> type = objects.iterator().next().getClass();
		PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(type);

		for (PropertyDescriptor property : properties) {
			Method writeMethod = property.getWriteMethod();
			if (writeMethod == null || writeMethod.getParameterTypes()[0].isPrimitive()
					|| !nullProps.contains(property.getName())) {
				continue;
			}
			setNullByWriteMethod(writeMethod, objects);
		}
	}

	public static void setPropertiesNullExcept(Object object, Set<String> excepts) {
		if (object == null) {
			return;
		}
		setPropertiesNullExcept(Collections.singletonList(object), excepts);
	}

	public static void setPropertiesNullExcept(Collection<?> objects, Set<String> excepts) {
		if (objects.isEmpty()) {
			return;
		}
		Class<?> type = objects.iterator().next().getClass();
		PropertyDescriptor[] properties = BeanUtils.getPropertyDescriptors(type);
		for (PropertyDescriptor property : properties) {
			Method writeMethod = property.getWriteMethod();
			if (writeMethod == null || writeMethod.getParameterTypes()[0].isPrimitive()
					|| excepts.contains(property.getName())) {
				continue;
			}
			setNullByWriteMethod(writeMethod, objects);
		}
	}

	public static <T> T copyProperties(Object source, T target) {
		BeanUtils.copyProperties(source, target);
		return target;
	}

	public static <T> T copyNotNullProperties(Object source, T target) {
		PropertyDescriptor[] srcProperties = BeanUtils.getPropertyDescriptors(source.getClass());
		for (PropertyDescriptor srcProperty : srcProperties) {
			Object value;
			try {
				value = srcProperty.getReadMethod().invoke(source);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new UnexpectedException("Failed to invoke read-method '" + srcProperty.getReadMethod(), e);
			}
			if (value == null) {
				continue;
			}
			PropertyDescriptor destProperty = BeanUtils.getPropertyDescriptor(target.getClass(), srcProperty.getName());
			if (matchWriteProperty(value, destProperty)) {
				try {
					destProperty.getWriteMethod().invoke(target, value);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new UnexpectedException("Failed to invoke write-method '" + destProperty.getWriteMethod(), e);
				}
			}
		}
		return target;
	}

	public static <T> T fromMap(Map<String, ?> map, Class<T> type) {
		String s = Jsons.write(map);
		return Jsons.read(s, type);
	}

	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		if (value instanceof String) {
			return ((String) value).isEmpty();
		}
		if (value instanceof Collection) {
			return ((Collection<?>) value).isEmpty();
		}
		if (value.getClass().isArray()) {
			return Array.getLength(value) == 0;
		}
		return false;
	}

	private static PropertyDescriptor getPropertyDescriptor(Object object, String propertyName) {
		PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(object.getClass(), propertyName);
		if (pd == null) {
			throw new UnexpectedException("No property '" + propertyName + "' defined for " + object.getClass());
		}
		return pd;
	}

	private static void setNullByWriteMethod(Method writeMethod, Collection<?> objects) {
		Object value = null;
		for (Object obj : objects) {
			try {
				writeMethod.invoke(obj, value);
			} catch (Exception ex) {
				throw new UnexpectedException(
						"Failed to invoke write-method '" + writeMethod + "' for object: " + Jsons.write(obj), ex);
			}
		}
	}

	private static boolean matchWriteProperty(Object value, PropertyDescriptor property) {
		if (property == null || property.getWriteMethod() == null) {
			return false;
		}
		Class<?> srcClass = value.getClass();
		Class<?> destClass = property.getPropertyType();
		return destClass == srcClass || destClass.isAssignableFrom(srcClass);
	}

}
