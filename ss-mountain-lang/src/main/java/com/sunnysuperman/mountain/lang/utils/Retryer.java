package com.sunnysuperman.mountain.lang.utils;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.exception.Exceptions;

public class Retryer {
	private static final Logger LOG = LoggerFactory.getLogger(Retryer.class);

	private int maxAttempts = 3;
	private long retryDelayInMills;
	private Logger logger = LOG;

	public Retryer() {
		super();
	}

	public <T> T call(Callable<T> callable) {
		Exception ex = null;
		int i = 0;
		while (true) {
			try {
				return callable.call();
			} catch (Exception e) {
				if (logger != null) {
					logger.error(null, e);
				}
				ex = e;
			}
			i++;
			if (i >= maxAttempts) {
				throw Exceptions.wrapRuntimeException(ex);
			}
			if (retryDelayInMills > 0) {
				ProcessUtil.sleep(retryDelayInMills);
			}
		}
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public Retryer setMaxAttempts(int maxAttempts) {
		if (maxAttempts <= 0) {
			throw new IllegalArgumentException("maxAttempts");
		}
		this.maxAttempts = maxAttempts;
		return this;
	}

	public long getRetryDelayInMills() {
		return retryDelayInMills;
	}

	public Retryer setRetryDelayInMills(long retryDelayInMills) {
		this.retryDelayInMills = retryDelayInMills;
		return this;
	}

	public Logger getLogger() {
		return logger;
	}

	public Retryer setLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

}
