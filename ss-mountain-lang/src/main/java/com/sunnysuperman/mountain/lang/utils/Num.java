package com.sunnysuperman.mountain.lang.utils;

import java.math.BigDecimal;

import com.sunnysuperman.mountain.lang.exception.FormatException;

public final class Num {
	public static final Byte BYTE_0 = 0;
	public static final Integer INT_0 = 0;
	public static final Long LONG_0 = 0L;

	public static final Byte BYTE_1 = 1;
	public static final Integer INT_1 = 1;
	public static final Long LONG_1 = 1L;

	private Num() {
	}

	public static Number parseNumber(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer || obj instanceof Float || obj instanceof Double || obj instanceof Long) {
			return (Number) obj;
		}
		obj = obj.toString();
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.isEmpty()) {
				return null;
			}
			if (theString.indexOf('.') >= 0) {
				return Double.valueOf(theString);
			} else {
				Long longObject = Long.valueOf(theString);
				long longValue = longObject.longValue();
				if (longValue > Integer.MAX_VALUE) {
					return longObject;
				} else {
					return Integer.valueOf((int) longValue);
				}
			}
		}
		throw new FormatException("Failed to parseNumber: " + obj);
	}

	public static Byte parseByte(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Byte) {
			return (Byte) obj;
		}
		if (obj instanceof Number) {
			return Byte.valueOf(((Number) obj).byteValue());
		}
		if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue() ? (byte) 1 : (byte) 0;
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.isEmpty()) {
				return null;
			}
			return Byte.valueOf(theString);
		}
		throw new FormatException("Failed to parseByte: " + obj);
	}

	public static byte parseByteValue(Object obj, byte defaultValue) {
		Byte v = parseByte(obj);
		return v == null ? defaultValue : v.byteValue();
	}

	public static Short parseShort(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Short) {
			return (Short) obj;
		}
		if (obj instanceof Number) {
			return Short.valueOf(((Number) obj).shortValue());
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.isEmpty()) {
				return null;
			}
			return Short.valueOf(theString);
		}
		throw new FormatException("Failed to parseShort: " + obj);
	}

	public static short parseShortValue(Object s, short defaultValue) {
		Short v = parseShort(s);
		return v == null ? defaultValue : v.shortValue();
	}

	public static Integer parseInteger(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		if (obj instanceof Number) {
			return Integer.valueOf(((Number) obj).intValue());
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.isEmpty()) {
				return null;
			}
			return Integer.valueOf(theString);
		}
		if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue() ? 1 : 0;
		}
		throw new FormatException("Failed to parseInteger: " + obj);
	}

	public static Integer parseRoundInteger(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer) {
			return (Integer) obj;
		}
		if (obj instanceof Number) {
			double d = ((Number) obj).doubleValue();
			return (int) Math.round(d);
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.isEmpty()) {
				return null;
			}
			double d = Double.parseDouble(theString);
			return (int) Math.round(d);
		}
		throw new FormatException("Failed to parseRoundInteger: " + obj);
	}

	public static int parseIntValue(Object obj, int defaultValue) {
		Integer v = parseInteger(obj);
		return v == null ? defaultValue : v.intValue();
	}

	public static int parseRoundIntValue(Object obj, int defaultValue) {
		Integer v = parseRoundInteger(obj);
		return v == null ? defaultValue : v.intValue();
	}

	public static Long parseLong(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Long) {
			return (Long) obj;
		}
		if (obj instanceof Number) {
			return Long.valueOf(((Number) obj).longValue());
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.isEmpty()) {
				return null;
			}
			return Long.valueOf(theString);
		}
		throw new FormatException("Failed to parseLong: " + obj);
	}

	public static long parseLongValue(Object obj, long defaultValue) {
		Long v = parseLong(obj);
		return v == null ? defaultValue : v.longValue();
	}

	public static Double parseDouble(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Double) {
			return (Double) obj;
		}
		if (obj instanceof Number) {
			return new BigDecimal(obj.toString()).doubleValue();
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.isEmpty()) {
				return null;
			}
			return Double.valueOf(theString);
		}
		throw new FormatException("Failed to parseDouble: " + obj);
	}

	public static double parseDoubleValue(Object obj, double defaultValue) {
		Double v = parseDouble(obj);
		return v == null ? defaultValue : v.doubleValue();
	}

	public static Float parseFloat(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Float) {
			return (Float) obj;
		}
		if (obj instanceof Number) {
			return new BigDecimal(obj.toString()).floatValue();
		}
		if (obj instanceof String) {
			String theString = (String) obj;
			if (theString.isEmpty()) {
				return null;
			}
			return Float.valueOf(theString);
		}
		throw new FormatException("Failed to parseFloat: " + obj);
	}

	public static float parseFloatValue(Object obj, float defaultValue) {
		Float v = parseFloat(obj);
		return v == null ? defaultValue : v.floatValue();
	}

	public static BigDecimal parseDecimal(Object obj, BigDecimal defaultValue) {
		if (obj == null) {
			return defaultValue;
		}
		if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		}
		if (obj instanceof Long) {
			return new BigDecimal((Long) obj);
		}
		if (obj instanceof Integer) {
			return new BigDecimal((Integer) obj);
		}
		String s = obj.toString();
		if (s.isEmpty()) {
			return defaultValue;
		}
		try {
			return new BigDecimal(s);
		} catch (Exception ex) {
			throw new FormatException("Failed to parseDecimal: " + obj, ex);
		}
	}

	public static BigDecimal parseDecimal(Object value) {
		return parseDecimal(value, null);
	}

	public static int long2int(long val) {
		return val > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) val;
	}

	public static byte getProgress(double completed, double total) {
		if (completed >= total) {
			return 100;
		}
		return (byte) Math.round(completed * 100 / total);
	}

	public static String pad(Number number, int length, boolean limitToMax) {
		String s = number.toString();
		int padLen = length - s.length();
		if (padLen > 0) {
			StringBuilder buf = new StringBuilder(length);
			for (int i = 0; i < padLen; i++) {
				buf.append('0');
			}
			buf.append(s);
			return buf.toString();
		}
		if (padLen == 0 || !limitToMax) {
			return s;
		}
		StringBuilder buf = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			buf.append('9');
		}
		return buf.toString();
	}

	public static String pad(Number number, int length) {
		return pad(number, length, false);
	}

}
