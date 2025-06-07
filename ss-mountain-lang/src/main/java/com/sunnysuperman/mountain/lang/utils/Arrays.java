package com.sunnysuperman.mountain.lang.utils;

import java.lang.reflect.Array;

public final class Arrays {

	private Arrays() {
	}

	public static boolean isEmpty(final Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean contains(final Object[] array, final Object objectToFind) {
		return indexOf(array, objectToFind) >= 0;
	}

	public static <T> int indexOf(final T[] array, final Object objectToFind) {
		return indexOf(array, objectToFind, 0);
	}

	public static int indexOf(final Object[] array, final Object objectToFind, int startIndex) {
		if (array == null) {
			return -1;
		}
		if (objectToFind == null) {
			for (int i = startIndex; i < array.length; i++) {
				if (array[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = startIndex; i < array.length; i++) {
				if (objectToFind.equals(array[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	public static <T> T[] concat(Class<T> clazz, T one, T[] others) {
		if (one == null) {
			throw new IllegalArgumentException("item should not be null");
		}
		@SuppressWarnings("unchecked")
		T[] newItems = (T[]) Array.newInstance(clazz, 1 + (others == null ? 0 : others.length));
		newItems[0] = one;
		if (others != null) {
			System.arraycopy(others, 0, newItems, 1, others.length);
		}
		return newItems;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] concat(T one, T[] others) {
		return concat((Class<T>) one.getClass(), one, others);
	}

	public static <T> T[] concat(Class<T> clazz, T[] one, T[] other) {
		@SuppressWarnings("unchecked")
		T[] target = (T[]) Array.newInstance(clazz,
				(one == null ? 0 : one.length) + (other == null ? 0 : other.length));
		if (one != null) {
			System.arraycopy(one, 0, target, 0, one.length);
		}
		if (other != null) {
			System.arraycopy(other, 0, target, (one == null ? 0 : one.length), other.length);
		}
		return target;
	}
}
