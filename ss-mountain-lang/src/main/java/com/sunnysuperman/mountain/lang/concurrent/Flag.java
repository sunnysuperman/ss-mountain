package com.sunnysuperman.mountain.lang.concurrent;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public class Flag {
	private String name;
	private Logger logger;
	private boolean cachedValue = false;
	private BooleanLock lock = new BooleanLock(false);

	public Flag() {
		super();
	}

	public Flag(String name, Logger logger) {
		super();
		this.name = Objects.requireNonNull(name);
		this.logger = Objects.requireNonNull(logger);
	}

	public Flag(Class<?> clazz, Logger logger) {
		super();
		this.name = clazz.getSimpleName();
		this.logger = Objects.requireNonNull(logger);
	}

	public boolean isTrue() {
		return cachedValue;
	}

	public boolean waitUntilTrue(long msTimeout) {
		if (cachedValue) {
			return true;
		}
		if (logger == null) {
			try {
				return lock.waitUntilTrue(msTimeout);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new UnexpectedException(e);
			}
		} else {
			long t1 = System.nanoTime();
			try {
				return lock.waitUntilTrue(msTimeout);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new UnexpectedException(e);
			} finally {
				if (logger.isInfoEnabled()) {
					long t2 = System.nanoTime();
					logger.info("Flag '{}' changed, wait {}ms until true", name,
							TimeUnit.NANOSECONDS.toMillis(t2 - t1));
				}
			}
		}
	}

	public void setAsTrue() {
		cachedValue = true;
		lock.setValue(true);
	}
}
