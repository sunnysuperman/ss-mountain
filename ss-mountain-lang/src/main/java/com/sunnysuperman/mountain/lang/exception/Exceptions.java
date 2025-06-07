package com.sunnysuperman.mountain.lang.exception;

public class Exceptions {

	private Exceptions() {
	}

	public static RuntimeException wrapRuntimeException(String message) {
		return new UnexpectedException(message);
	}

	public static RuntimeException wrapRuntimeException(Throwable e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new UnexpectedException(e);
	}

	public static RuntimeException wrapRuntimeException(String message, Throwable e) {
		if (message == null) {
			return wrapRuntimeException(e);
		}
		return new UnexpectedException(message, e);
	}

}
