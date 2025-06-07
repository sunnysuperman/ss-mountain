package com.sunnysuperman.mountain.lang.exception.service;

import org.slf4j.Logger;

public class ServiceLogger {
	private Logger logger;

	public ServiceLogger(Logger logger) {
		super();
		this.logger = logger;
	}

	public void logExceptServiceException(final String msg, final Throwable ex) {
		if (ex == null) {
			if (msg != null) {
				logger.error(msg);
			}
			return;
		}
		Throwable cause = ex;
		while (cause instanceof ServiceException) {
			cause = cause.getCause();
		}
		if (msg != null || cause != null) {
			logger.error(msg, cause);
		}
	}

	public void logExceptServiceException(final Throwable ex) {
		logExceptServiceException(null, ex);
	}

}
