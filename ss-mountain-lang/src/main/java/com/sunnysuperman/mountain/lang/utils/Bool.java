package com.sunnysuperman.mountain.lang.utils;

import com.sunnysuperman.mountain.lang.exception.FormatException;

public final class Bool {

	private Bool() {
	}

	public static Boolean parse(Object obj) {
		Boolean nil = null;
		if (obj == null) {
			return nil;
		}
		if (obj instanceof Boolean) {
			return ((Boolean) obj);
		}
		if (obj instanceof Number) {
			obj = obj.toString();
		}
		if (obj instanceof String) {
			String strValue = (String) obj;
			if (strValue.isEmpty()) {
				return nil;
			}
			if (strValue.equalsIgnoreCase("true") || strValue.equals("1")) {
				return Boolean.TRUE;
			}
			if (strValue.equalsIgnoreCase("false") || strValue.equals("0")) {
				return Boolean.FALSE;
			}
		}
		throw new FormatException("Failed to parse boolean: " + obj);
	}

	public static Boolean parse(Object obj, Boolean defaultValue) {
		Boolean bool = parse(obj);
		if (bool == null) {
			return defaultValue;
		}
		return bool;
	}

	public static boolean parseBoolValue(Object obj, boolean defaultValue) {
		Boolean bool = parse(obj);
		if (bool == null) {
			return defaultValue;
		}
		return bool;
	}

}
