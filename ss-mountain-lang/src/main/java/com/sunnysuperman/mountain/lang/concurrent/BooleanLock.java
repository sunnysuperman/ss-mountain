package com.sunnysuperman.mountain.lang.concurrent;

public class BooleanLock {
	private boolean value;

	public BooleanLock(boolean initialValue) {
		this.value = initialValue;
	}

	public BooleanLock() {
		this(false);
	}

	public synchronized boolean setValue(boolean newValue) {
		if (value != newValue) {
			value = newValue;
			notifyAll();
			return true;
		}
		return false;
	}

	public synchronized boolean waitUntilStateIs(boolean state, long msTimeout) throws InterruptedException {
		if (msTimeout == 0L) {
			while (value != state) {
				wait();
			}
			return true;
		}
		// 只等待指定的时间
		long endTime = System.currentTimeMillis() + msTimeout;
		long msRemaining = msTimeout;
		while ((value != state) && (msRemaining > 0L)) {
			wait(msRemaining);
			msRemaining = endTime - System.currentTimeMillis();
		}
		// 可能满足了条件(返回真),也可能已经超时了(返回假)
		return (value == state);
	}

	public synchronized boolean waitUntilTrue(long msTimeout) throws InterruptedException {
		return waitUntilStateIs(true, msTimeout);
	}

	public synchronized boolean waitUntilFalse(long msTimeout) throws InterruptedException {
		return waitUntilStateIs(false, msTimeout);
	}

	public synchronized boolean isTrue() {
		return value;
	}

	public synchronized boolean isFalse() {
		return !value;
	}

	public synchronized boolean waitUntilFalseThenSetTrue(long msTimeout) throws InterruptedException {
		boolean success = waitUntilFalse(msTimeout);
		if (success) {
			setValue(true);
			return true;
		}
		return false;
	}

	public synchronized boolean waitToSetTrue(long msTimeout) throws InterruptedException {
		boolean success = waitUntilTrue(msTimeout);
		if (success) {
			return true;
		}
		setValue(true);
		return false;
	}

	public synchronized boolean waitToSetFalse(long msTimeout) throws InterruptedException {
		boolean success = waitUntilFalse(msTimeout);
		if (success) {
			return true;
		}
		setValue(false);
		return false;
	}
}
