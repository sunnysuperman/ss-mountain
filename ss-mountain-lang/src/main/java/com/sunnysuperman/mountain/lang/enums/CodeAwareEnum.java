package com.sunnysuperman.mountain.lang.enums;

import java.lang.reflect.InvocationTargetException;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public interface CodeAwareEnum {

	public byte code();

	@SuppressWarnings("unchecked")
	public static <T extends CodeAwareEnum> T fromCode(Class<T> type, byte code) {
		T[] values;
		try {
			values = (T[]) type.getMethod("values").invoke(null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new UnexpectedException(e);
		}
		for (T value : values) {
			if (value.code() == code) {
				return value;
			}
		}
		return null;
	}

}
