package com.sunnysuperman.mountain.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLogger {
	private static final Logger LOG = LoggerFactory.getLogger(JobLogger.class);
	private static final boolean INFO_ENABLED = LOG.isInfoEnabled();

	private JobLogger() {
		// static only
	}

	public static boolean isInfoEnabled() {
		return INFO_ENABLED;
	}

	public static void info(String msg) {
		LOG.info(msg);
	}

	public static void info(String format, Object... args) {
		LOG.info(format, args);
	}

	public static void warn(String msg) {
		LOG.warn(msg);
	}

	public static void warn(String format, Object... args) {
		LOG.warn(format, args);
	}

	public static void error(String msg) {
		LOG.error(msg);
	}

	public static void error(String format, Object... args) {
		LOG.error(format, args);
	}

	public static void error(Throwable ex) {
		LOG.error(null, ex);
	}

	public static void error(String msg, Throwable ex) {
		LOG.error(msg, ex);
	}
}
