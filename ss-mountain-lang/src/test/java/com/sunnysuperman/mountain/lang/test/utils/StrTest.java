package com.sunnysuperman.mountain.lang.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.utils.Str;

class StrTest {

	@Test
	void testParse() {
		// 测试 null 对象
		assertNull(Str.parse(null));

		// 测试字符串对象
		assertEquals("hello", Str.parse("hello"));

		// 测试字节数组
		byte[] bytes = "test".getBytes(Str.UTF8_CHARSET);
		assertEquals("test", Str.parse(bytes));

		// 测试其他对象
		assertEquals("123", Str.parse(123));
	}

	@Test
	void testParseWithDefault() {
		// 测试 null 对象
		assertEquals("default", Str.parse(null, "default"));

		// 测试非null对象
		assertEquals("value", Str.parse("value", "default"));
	}

	@Test
	void testIsEmpty() {
		// 测试 null
		assertTrue(Str.isEmpty(null));

		// 测试空字符串
		assertTrue(Str.isEmpty(""));

		// 测试非空字符串
		assertFalse(Str.isEmpty(" "));
		assertFalse(Str.isEmpty("a"));
	}

	@Test
	void testIsNotEmpty() {
		// 测试 null
		assertFalse(Str.isNotEmpty(null));

		// 测试空字符串
		assertFalse(Str.isNotEmpty(""));

		// 测试非空字符串
		assertTrue(Str.isNotEmpty(" "));
		assertTrue(Str.isNotEmpty("a"));
	}

	@Test
	void testOr() {
		// 测试两个参数
		assertEquals("first", Str.or("first", "second"));
		assertEquals("second", Str.or("", "second"));
		assertEquals("second", Str.or(null, "second"));

		// 测试三个参数
		assertEquals("first", Str.or("first", "second", "third"));
		assertEquals("second", Str.or("", "second", "third"));
		assertEquals("third", Str.or("", "", "third"));
	}

	@Test
	void testIsBlank() {
		// 测试 null
		assertTrue(Str.isBlank(null));

		// 测试空字符串
		assertTrue(Str.isBlank(""));

		// 测试空白字符串
		assertTrue(Str.isBlank(" "));
		assertTrue(Str.isBlank("  "));
		assertTrue(Str.isBlank("\t"));
		assertTrue(Str.isBlank("\n"));

		// 测试非空白字符串
		assertFalse(Str.isBlank("a"));
		assertFalse(Str.isBlank(" a "));
	}

	@Test
	void testIsNotBlank() {
		// 测试 null
		assertFalse(Str.isNotBlank(null));

		// 测试空字符串
		assertFalse(Str.isNotBlank(""));

		// 测试空白字符串
		assertFalse(Str.isNotBlank(" "));
		assertFalse(Str.isNotBlank("  "));

		// 测试非空白字符串
		assertTrue(Str.isNotBlank("a"));
		assertTrue(Str.isNotBlank(" a "));
	}

	@Test
	void testTrim() {
		// 测试 null
		assertNull(Str.trim(null));

		// 测试空字符串
		assertEquals("", Str.trim(""));

		// 测试前后空格
		assertEquals("a", Str.trim(" a "));
		assertEquals("a", Str.trim("\ta\t"));
	}

	@Test
	void testTrimToNull() {
		// 测试 null
		assertNull(Str.trimToNull(null));

		// 测试空字符串
		assertNull(Str.trimToNull(""));
		assertNull(Str.trimToNull(" "));

		// 测试非空字符串
		assertEquals("a", Str.trimToNull(" a "));
	}

	@Test
	void testTrimToEmpty() {
		// 测试 null
		assertEquals("", Str.trimToEmpty(null));

		// 测试空字符串
		assertEquals("", Str.trimToEmpty(""));
		assertEquals("", Str.trimToEmpty(" "));

		// 测试非空字符串
		assertEquals("a", Str.trimToEmpty(" a "));
	}

	@Test
	void testEmptyToNull() {
		// 测试 null
		assertNull(Str.emptyToNull(null));

		// 测试空字符串
		assertNull(Str.emptyToNull(""));

		// 测试非空字符串
		assertEquals("a", Str.emptyToNull("a"));
	}

	@Test
	void testNullToEmpty() {
		// 测试 null
		assertEquals("", Str.nullToEmpty(null));

		// 测试非null
		assertEquals("a", Str.nullToEmpty("a"));
		assertEquals("", Str.nullToEmpty(""));
	}

	@Test
	void testRandomString() {
		// 测试随机字符串生成
		String salt = "abc";
		int length = 5;
		String result = Str.randomString(salt, length);
		assertEquals(length, result.length());
		for (char c : result.toCharArray()) {
			assertTrue(salt.indexOf(c) >= 0);
		}

		// 测试字符数组版本
		char[] saltChars = { 'x', 'y', 'z' };
		result = Str.randomString(saltChars, length);
		assertEquals(length, result.length());
		for (char c : result.toCharArray()) {
			assertTrue(new String(saltChars).indexOf(c) >= 0);
		}
	}

	@Test
	void testRandomNumeric() {
		// 测试随机数字生成
		String result = Str.randomNumeric(10);
		assertEquals(10, result.length());
		assertTrue(Str.isNumeric(result));
	}

	@Test
	void testRandomAlphanumeric() {
		// 测试随机字母数字生成
		String result = Str.randomAlphanumeric(10);
		assertEquals(10, result.length());
		assertTrue(Str.isAlphanumeric(result));
	}

	@Test
	void testIsTargetString() {
		// 测试目标字符串检查
		String salt = "abc123";
		assertTrue(Str.isTargetString(salt, "a1"));
		assertFalse(Str.isTargetString(salt, "a1d"));

		// 测试边界情况
		assertFalse(Str.isTargetString(salt, null));
		assertFalse(Str.isTargetString(salt, ""));
	}

	@Test
	void testIsTargetChar() {
		// 测试目标字符检查
		String salt = "abc123";
		assertTrue(Str.isTargetChar(salt, 'a'));
		assertFalse(Str.isTargetChar(salt, 'd'));
	}

	@Test
	void testIsUpperCaseAlpha() {
		// 测试字符
		assertTrue(Str.isUpperCaseAlpha('A'));
		assertFalse(Str.isUpperCaseAlpha('a'));
		assertFalse(Str.isUpperCaseAlpha('1'));

		// 测试字符串
		assertTrue(Str.isUpperCaseAlpha("ABC"));
		assertFalse(Str.isUpperCaseAlpha("ABc"));
		assertFalse(Str.isUpperCaseAlpha(""));
		assertFalse(Str.isUpperCaseAlpha(null));
	}

	@Test
	void testIsLowerCaseAlpha() {
		// 测试字符
		assertTrue(Str.isLowerCaseAlpha('a'));
		assertFalse(Str.isLowerCaseAlpha('A'));
		assertFalse(Str.isLowerCaseAlpha('1'));
	}

	@Test
	void testIsAlpha() {
		// 测试字符
		assertTrue(Str.isAlpha('a'));
		assertTrue(Str.isAlpha('A'));
		assertFalse(Str.isAlpha('1'));
	}

	@Test
	void testIsNumeric() {
		// 测试字符
		assertTrue(Str.isNumeric('0'));
		assertFalse(Str.isNumeric('a'));

		// 测试字符串
		assertTrue(Str.isNumeric("123"));
		assertFalse(Str.isNumeric("123a"));
		assertFalse(Str.isNumeric(""));
		assertFalse(Str.isNumeric(null));
	}

	@Test
	void testIsAlphanumeric() {
		// 测试字符串
		assertTrue(Str.isAlphanumeric("a1B2"));
		assertFalse(Str.isAlphanumeric("a1B2!"));
	}

	@Test
	void testSplit() {
		// 测试空字符串
		assertEquals(Collections.emptyList(), Str.split(""));
		assertEquals(Collections.emptyList(), Str.split(null));

		// 测试默认分隔符
		assertEquals(Arrays.asList("a", "b", "c"), Str.split("a,b,c"));

		// 测试自定义分隔符
		assertEquals(Arrays.asList("a", "bc", "d"), Str.split("a|bc|d", "|"));
		assertEquals(Arrays.asList("a", "bc", "d"), Str.split("a|bc|d|", "|"));
		assertEquals(Arrays.asList("a", "bc", "de"), Str.split("a|bc|de", "|"));
	}

	@Test
	void testSplitWithLimit() {
		// 测试空字符串
		assertEquals(Collections.emptyList(), Str.split("", ",", 2));
		assertEquals(Collections.emptyList(), Str.split(null, ",", 2));

		// 基本测试
		assertEquals(Arrays.asList("a", "b"), Str.split("a,b,c", ",", 2));
		assertEquals(Arrays.asList("a"), Str.split("a,b,c", ",", 1));

		// 限制大于实际分割次数
		assertEquals(Arrays.asList("a", "b"), Str.split("a,b", ",", 3));
		assertEquals(Arrays.asList("abc"), Str.split("abc", ",", 2));

		// 刚好达到limit
		assertEquals(Arrays.asList("a", "b", "c"), Str.split("a,b,c", ",", 3));

		// 不限
		assertEquals(Arrays.asList("a", "bc", "d"), Str.split("a,bc,d", ",", -1));
		assertEquals(Arrays.asList("a", "bc", "d"), Str.split("a,bc,d", ",", 0));
	}

	@Test
	void testJoin() {
		// 测试数组
		String[] array = { "a", "b", "c" };
		assertEquals("a,b,c", Str.join(array));
		assertEquals("a|b|c", Str.join(array, "|"));

		// 测试集合
		List<String> list = Arrays.asList("x", "y", "z");
		assertEquals("x,y,z", Str.join(list));
		assertEquals("x|y|z", Str.join(list, "|"));

		// 测试空集合
		assertNull(Str.join(Collections.emptyList()));
		assertNull(Str.join(new String[0]));
	}

	@Test
	void testParseUnicode() {
		// 测试Unicode解析
		assertEquals("你好", Str.parseUnicode("\\u4f60\\u597d"));

		// 测试非Unicode字符串
		assertEquals("hello", Str.parseUnicode("hello"));

		// 测试空字符串
		assertEquals("", Str.parseUnicode(""));
		assertNull(Str.parseUnicode(null));
	}

	@Test
	void testEscape() {
		// 测试转义字符
		assertEquals("\n", Str.escape("\\n"));
		assertEquals("\t", Str.escape("\\t"));
		assertEquals("hello", Str.escape("hello"));

		// 测试空字符串
		assertEquals("", Str.escape(""));
		assertNull(Str.escape(null));
	}

	@Test
	void testTruncate() {
		// 测试截断
		assertEquals("hello", Str.truncate("hello", 10));
		assertEquals("hello", Str.truncate("hello world", 5));

		// 测试空字符串
		assertNull(Str.truncate("", 5));
		assertNull(Str.truncate(null, 5));
	}

	@Test
	void testTruncateWithEllipses() {
		// 测试带省略号的截断
		assertEquals("hello...", Str.truncateWithEllipses("hello world", 8));
		assertEquals("hello", Str.truncateWithEllipses("hello", 10));

		// 测试边界情况
		assertEquals("...", Str.truncateWithEllipses("hello", 3));
		assertNull(Str.truncateWithEllipses(null, 5));
	}

	@Test
	void testCapitalize() {
		// 测试首字母大写
		assertEquals("Hello", Str.capitalize("hello"));
		assertEquals("H", Str.capitalize("h"));
		assertEquals("", Str.capitalize(""));
		assertNull(Str.capitalize(null));
	}

	@Test
	void testUnderline2Camel() {
		// 测试下划线转驼峰
		assertEquals("helloWorld", Str.underline2Camel("hello_world"));
		assertEquals("hello", Str.underline2Camel("hello"));
		assertNull(Str.underline2Camel(null));
	}

	@Test
	void testCamel2Underline() {
		// 测试驼峰转下划线
		assertEquals("hello_world", Str.camel2Underline("helloWorld"));
		assertEquals("hello", Str.camel2Underline("hello"));
		assertNull(Str.camel2Underline(null));
	}

	@Test
	void testMask() {
		// 测试脱敏
		assertEquals("a*c", Str.mask("abc"));
		assertEquals("ab**ef", Str.mask("abcdef"));
		assertEquals("*", Str.mask("a"));
		assertEquals("", Str.mask(""));
		assertNull(Str.mask(null));
	}

}