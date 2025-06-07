package com.sunnysuperman.mountain.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.httpclient.HttpClient;
import com.sunnysuperman.mountain.lang.exception.Exceptions;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.exception.service.ServiceException;
import com.sunnysuperman.mountain.lang.id.ObjectIdGeneratorFactory;
import com.sunnysuperman.mountain.lang.utils.Colls;
import com.sunnysuperman.mountain.lang.utils.IOUtil;
import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.ProcessUtil;
import com.sunnysuperman.mountain.lang.utils.Str;

public abstract class Tester {
	private static final Logger LOG = LoggerFactory.getLogger(Tester.class);
	private static final Scanner SCANNER = new Scanner(System.in);

	public static void assertTrue(boolean condition) {
		Assertions.assertTrue(condition);
	}

	public static void assertTrue(boolean condition, String message) {
		Assertions.assertTrue(condition, message);
	}

	public static void assertFalse(boolean condition) {
		Assertions.assertFalse(condition);
	}

	public static void assertFalse(boolean condition, String message) {
		Assertions.assertFalse(condition, message);
	}

	public static void assertNull(Object actual) {
		Assertions.assertNull(actual);
	}

	public static void assertNotNull(Object actual) {
		Assertions.assertNotNull(actual);
	}

	public static void assertEquals(Object expected, Object actual) {
		Assertions.assertEquals(expected, actual);
	}

	public static void assertNotEquals(Object unexpected, Object actual) {
		Assertions.assertNotEquals(unexpected, actual);
	}

	public static void assertEmpty(Collection<?> collection) {
		assertTrue(Colls.isEmpty(collection));
	}

	public static void assertNotEmpty(Collection<?> collection) {
		assertTrue(Colls.isNotEmpty(collection));
	}

	public static void assertEmpty(String s) {
		assertTrue(Str.isEmpty(s));
	}

	public static void assertNotEmpty(String s) {
		assertTrue(Str.isNotEmpty(s));
	}

	public static void assertDecimalEquals(BigDecimal b1, BigDecimal b2) {
		assertTrue(b1.compareTo(b2) == 0);
	}

	public static boolean listEquals(List<?> list1, List<?> list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		for (int i = 0; i < list1.size(); i++) {
			Object item1 = list1.get(i);
			Object item2 = list2.get(i);
			if (!Objects.equals(item1, item2)) {
				return false;
			}
		}
		return true;
	}

	public static void assertListEquals(List<?> list1, List<?> list2) {
		assertTrue(listEquals(list1, list2));
	}

	public static void assertObjectEquals(Object vo1, Object vo2) {
		assertEquals(Jsons.write(vo1), Jsons.write(vo2));
	}

	protected final void waitUntilTrue(int maxWaitSeconds, Asserts a) {
		long t1 = System.currentTimeMillis();
		long maxWaitMills = maxWaitSeconds * 1000L;
		while (true) {
			try {
				if (a.asserts()) {
					return;
				}
			} catch (ServiceException se) {
				throw se;
			} catch (Exception e) {
				throw Exceptions.wrapRuntimeException(e);
			}
			long t2 = System.currentTimeMillis();
			if (t2 - t1 >= maxWaitMills) {
				break;
			}
			ProcessUtil.sleep(1000);
		}
		assertTrue(false);
	}

	protected final void waitAMoment(long duration) {
		ProcessUtil.sleep(duration);
	}

	public final File downloadFile(String url) {
		try {
			File file = newFile(null);
			new HttpClient(1, 60L).download(url, new FileOutputStream(file));
			return file;
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}

	public final File newFile(String suffix) throws IOException {
		File file = new File("/tmp/" + ObjectIdGeneratorFactory.create().generate() + "." + suffix);
		IOUtil.deleteFile(file);
		IOUtil.createFile(file);
		return file;
	}

	protected void openFile(File file) {
		assertTrue(file.exists());
		try {
			Runtime.getRuntime().exec("open " + file.getAbsolutePath());
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}

	protected void print(String msg, Object... args) {
		LOG.info(msg, args);
	}

	protected String prompt(String msg) {
		print(msg);
		return SCANNER.next();
	}
}
