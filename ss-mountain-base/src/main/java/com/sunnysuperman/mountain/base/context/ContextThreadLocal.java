package com.sunnysuperman.mountain.base.context;

public class ContextThreadLocal extends ThreadLocal<Context> {

	private ContextThreadLocal() {
	}

	private static final ContextThreadLocal INSTANCE = new ContextThreadLocal();

	public static ContextThreadLocal getInstance() {
		return INSTANCE;
	}
}
