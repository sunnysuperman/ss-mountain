package com.sunnysuperman.mountain.lang.utils;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Str {
	public static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
	public static final String UTF8 = StandardCharsets.UTF_8.name();
	public static final String EMPTY = "";
	public static final String COMMA = ",";
	public static final String NUMERIC = "0123456789";
	public static final String ALPHA_NUMERIC = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ELLIPSIS = "...";
	private static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
	private static final Random RANDOM = new SecureRandom();
	private static final Map<Character, Character> ESCAPE_MAPPING = new HashMap<>();

	static {
		ESCAPE_MAPPING.put('n', '\n');
		ESCAPE_MAPPING.put('t', '\t');
		ESCAPE_MAPPING.put('r', '\r');
		ESCAPE_MAPPING.put('f', '\f');
		ESCAPE_MAPPING.put('s', ' ');
	}

	private Str() {
	}

	public static String parse(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		Class<?> clazz = obj.getClass();
		if (clazz.isArray() && clazz.getComponentType().equals(byte.class)) {
			return new String((byte[]) obj, UTF8_CHARSET);
		}
		return obj.toString();
	}

	public static String parse(Object obj, String defaultValue) {
		String s = parse(obj);
		if (s == null) {
			return defaultValue;
		}
		return s;
	}

	public static boolean isEmpty(CharSequence cs) {
		return cs == null || cs.length() == 0;
	}

	public static boolean isNotEmpty(CharSequence cs) {
		return cs != null && cs.length() > 0;
	}

	public static String or(String s1, String s2) {
		if (s1 != null && !s1.isEmpty()) {
			return s1;
		}
		return s2;
	}

	public static String or(String s1, String s2, String s3) {
		if (s1 != null && !s1.isEmpty()) {
			return s1;
		}
		if (s2 != null && !s2.isEmpty()) {
			return s2;
		}
		return s3;
	}

	/**
	 * 判断字符串是否为空
	 */
	public static boolean isBlank(CharSequence cs) {
		int len;
		if (cs == null || (len = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < len; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串非空
	 */
	public static boolean isNotBlank(CharSequence cs) {
		return !isBlank(cs);
	}

	/**
	 * 过滤字符串两端的空字符
	 */
	public static String trim(String str) {
		return str != null ? str.trim() : null;
	}

	/**
	 * 过滤字符串两端的空字符串，最终为空返回null
	 */
	public static String trimToNull(String str) {
		String ts = trim(str);
		return isEmpty(ts) ? null : ts;
	}

	/**
	 * 过滤字符串两端的空字符串，如果为null返回空字串
	 */
	public static String trimToEmpty(String str) {
		return str != null ? str.trim() : EMPTY;
	}

	/**
	 * 若字符串为null或者字符串长度为0，返回null；否则，返回字符串本身
	 */
	public static String emptyToNull(String str) {
		if (str == null || str.isEmpty()) {
			return null;
		}
		return str;
	}

	/**
	 * 若字符串为null，返回空字符串；否则，返回字符串本身
	 */
	public static String nullToEmpty(String str) {
		if (str == null) {
			return EMPTY;
		}
		return str;
	}

	public static Random getRandomInstance() {
		return RANDOM;
	}

	public static String randomString(String salt, int length) {
		StringBuilder sb = new StringBuilder(length);
		int saltLength = salt.length();
		for (int i = 0; i < length; i++) {
			sb.append(salt.charAt(RANDOM.nextInt(saltLength)));
		}
		return sb.toString();
	}

	public static String randomString(char[] salt, int length) {
		StringBuilder sb = new StringBuilder(length);
		int saltLength = salt.length;
		for (int i = 0; i < length; i++) {
			sb.append(salt[RANDOM.nextInt(saltLength)]);
		}
		return sb.toString();
	}

	public static String randomNumeric(int length) {
		return randomString(NUMERIC, length);
	}

	public static String randomAlphanumeric(int length) {
		return randomString(ALPHA_NUMERIC, length);
	}

	/**
	 * 判断字符串是否存在于目标字符串中
	 */
	public static boolean isTargetString(String salt, String s) {
		if (s == null) {
			return false;
		}
		int len = s.length();
		if (len == 0) {
			return false;
		}
		for (int i = 0; i < len; i++) {
			if (!isTargetChar(salt, s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符是否存在于字符串中
	 */
	public static boolean isTargetChar(String salt, char c) {
		for (int i = 0; i < salt.length(); i++) {
			if (salt.charAt(i) == c) {
				return true;
			}
		}
		return false;
	}

	public static boolean isUpperCaseAlpha(char c) {
		return c >= 'A' && c <= 'Z';
	}

	public static boolean isUpperCaseAlpha(String s) {
		if (isEmpty(s)) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c < 65 || c > 90) {
				return false;
			}
		}
		return true;
	}

	public static boolean isLowerCaseAlpha(char c) {
		return c >= 'a' && c <= 'z';
	}

	public static boolean isAlpha(char c) {
		// 大写字母
		if (c >= 65 && c <= 90) {
			return true;
		}
		// 小写字母
		return c >= 97 && c <= 122;
	}

	/**
	 * 判断字符是否为数字
	 * 
	 * @param c 待判断字符
	 * @return 若是数字，则返回true；否则，返回false
	 */
	public static boolean isNumeric(char c) {
		return c >= 48 && c <= 57;
	}

	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumeric(String s) {
		if (s == null) {
			return false;
		}
		int len = s.length();
		if (len == 0) {
			return false;
		}
		char c;
		for (int i = 0; i < len; i++) {
			c = s.charAt(i);
			if (c >= 48 && c <= 57) {
				continue;
			}
			return false;
		}
		return true;
	}

	public static boolean isAlphanumeric(String s) {
		return isTargetString(ALPHA_NUMERIC, s);
	}

	/**
	 * 按照字面分隔符分割字符串
	 * 
	 * @param str           要分割的字符串
	 * @param delimiter     分隔符（字面值，不作为正则表达式处理）
	 * @param limit         最大分割次数(<=0表示不限)
	 * @param preserveEmpty 是否保留分隔的空字串(开头结尾的不保留)
	 * @return 分割后的字符串列表
	 */
	public static List<String> split(final String str, final String delimiter, final int limit,
			final boolean preserveEmpty) {
		if (isEmpty(str) || isEmpty(delimiter)) {
			return Collections.emptyList();
		}
		List<String> result = new ArrayList<>();
		int delimiterLength = delimiter.length();
		int start = 0;
		int end;
		boolean stopped = false;
		while ((end = str.indexOf(delimiter, start)) != -1) {
			if (end > start || (preserveEmpty && start > 0)) {
				result.add(str.substring(start, end));
				if (limit > 0 && result.size() >= limit) {
					stopped = true;
					break;
				}
			}
			start = end + delimiterLength;
		}
		// 添加最后一个部分
		if (!stopped && start < str.length()) {
			result.add(str.substring(start));
		}
		return result;
	}

	/**
	 * 按照字面分隔符分割字符串，并限制分割次数
	 * 
	 * @param str       要分割的字符串
	 * @param delimiter 分隔符
	 * @param limit     最大分割次数(<=0表示不限)
	 * @return 分割后的字符串列表
	 */
	public static List<String> split(String str, String delimiter, int limit) {
		return split(str, delimiter, limit, false);
	}

	/**
	 * 按照字面分隔符分割字符串
	 * 
	 * @param str       要分割的字符串
	 * @param delimiter 分隔符
	 * @return 分割后的字符串列表
	 */
	public static List<String> split(String str, String delimiter) {
		return split(str, delimiter, -1, false);
	}

	/**
	 * 用逗号分割字符串
	 * 
	 * @param str 要分割的字符串
	 * @return 分割后的字符串列表
	 */
	public static List<String> split(final String str) {
		return split(str, COMMA, -1, false);
	}

	/** 拼接(逗号分隔) **/
	public static String join(final Object collection) {
		return join(collection, COMMA);
	}

	/** 拼接 **/
	public static String join(final Object collection, final String delimiter) {
		if (collection == null) {
			return null;
		}
		if (collection.getClass().isArray()) {
			return joinArray(collection, delimiter);
		} else {
			return joinCollection(collection, delimiter);
		}
	}

	/** 拼接集合 **/
	public static String joinArray(final Object collection, final String delimiter) {
		int length = Array.getLength(collection);
		if (length == 0) {
			return null;
		}
		boolean appendSeparator = Str.isNotEmpty(delimiter);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i > 0 && appendSeparator) {
				buf.append(delimiter);
			}
			buf.append(Array.get(collection, i).toString());
		}
		return buf.toString();
	}

	/** 拼接 **/
	public static String joinCollection(final Object collection, final String delimiter) {
		StringBuilder buf = new StringBuilder();
		Iterable<?> iterable = (Iterable<?>) collection;
		boolean appendSeparator = Str.isNotEmpty(delimiter);
		int i = 0;
		for (Iterator<?> iter = iterable.iterator(); iter.hasNext();) {
			Object item = iter.next();
			if (i > 0 && appendSeparator) {
				buf.append(delimiter);
			}
			buf.append(item.toString());
			i++;
		}
		if (i == 0) {
			return null;
		}
		return buf.toString();
	}

	public static String parseUnicode(final String input) {
		if (isEmpty(input)) {
			return input;
		}
		Matcher matcher = UNICODE_PATTERN.matcher(input);
		StringBuffer buf = new StringBuffer();
		while (matcher.find()) {
			char ch = (char) Integer.parseInt(matcher.group(1), 16);
			matcher.appendReplacement(buf, String.valueOf(ch));
		}
		matcher.appendTail(buf);
		return buf.toString();
	}

	/** 转义 **/
	public static String escape(final String input) {
		if (isEmpty(input)) {
			return input;
		}
		StringBuilder result = new StringBuilder(input.length());
		int length = input.length();
		int currentIndex = 0;
		while (currentIndex < length) {
			char currentChar = input.charAt(currentIndex);
			if (currentChar == '\\' && currentIndex < length - 1) {
				char nextChar = input.charAt(currentIndex + 1);
				Character replacement = ESCAPE_MAPPING.get(nextChar);
				if (replacement != null) {
					result.append(replacement.charValue());
					currentIndex += 2;
				} else if (nextChar == '0') {
					currentIndex += 2;
				} else {
					result.append(currentChar);
					currentIndex++;
				}
			} else {
				result.append(currentChar);
				currentIndex++;
			}
		}
		return result.toString();
	}

	/** 截断 **/
	public static String truncate(String input, final int maxLength) {
		if (input == null) {
			return null;
		}
		input = input.trim();
		int len = input.length();
		if (len == 0) {
			return null;
		}
		if (len > maxLength) {
			input = input.substring(0, maxLength);
		}
		return input;
	}

	/** 截断(补省略号) **/
	public static String truncateWithEllipses(final String input, final int maxLength) {
		if (input == null) {
			return input;
		}
		int len = input.length();
		if (len <= maxLength || len <= ELLIPSIS.length()) {
			return input;
		}
		return input.substring(0, maxLength - ELLIPSIS.length()) + ELLIPSIS;
	}

	/** 首字母转大写 **/
	public static String capitalize(final String input) {
		if (isEmpty(input)) {
			return input;
		}
		if (input.length() > 1) {
			return Character.toUpperCase(input.charAt(0)) + input.substring(1);
		}
		return input.toUpperCase();
	}

	/** 下划线转驼峰 **/
	public static String underline2Camel(final String input) {
		if (input == null || input.indexOf('_') < 0) {
			return input;
		}
		StringBuilder result = new StringBuilder(input.length());
		boolean nextUpper = false;
		for (int i = 0; i < input.length(); i++) {
			char currentChar = input.charAt(i);
			if (currentChar == '_') {
				nextUpper = true;
			} else {
				result.append(nextUpper ? Character.toUpperCase(currentChar) : currentChar);
				nextUpper = false;
			}
		}
		return result.toString();
	}

	/** 驼峰转下划线 **/
	public static String camel2Underline(final String input) {
		if (isEmpty(input)) {
			return input;
		}
		char c;
		int upperSize = 0;
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (Character.isUpperCase(c)) {
				upperSize++;
			}
		}
		if (upperSize == 0) {
			return input;
		}
		StringBuilder buf = new StringBuilder(input.length() + upperSize);
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (Character.isUpperCase(c)) {
				buf.append('_');
				buf.append(Character.toLowerCase(c));
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	/** 打码脱敏 **/
	public static String mask(String input) {
		if (isEmpty(input)) {
			return input;
		}
		final int len = input.length();
		final String placeholder = "*";
		int maskLen = (int) Math.ceil(len / 3d);
		if (maskLen == 0) {
			return placeholder;
		}
		if (maskLen == len) {
			return placeholder;
		}
		int startLen = (int) Math.ceil((len - maskLen) / 2d);
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < startLen; i++) {
			buf.append(input.charAt(i));
		}
		for (int i = 0; i < maskLen; i++) {
			buf.append(placeholder);
		}
		int tailOffset = startLen + maskLen;
		for (int i = tailOffset; i < len; i++) {
			buf.append(input.charAt(i));
		}
		return buf.toString();
	}

}
