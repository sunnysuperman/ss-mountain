package com.sunnysuperman.mountain.lang.exception;

public class FatalException extends RuntimeException {
	private static final long serialVersionUID = -1L;

	public FatalException(String message, Throwable cause) {
		super(message, cause);
	}

	public FatalException(String message) {
		super(message);
	}

	public FatalException(Throwable cause) {
		super(cause);
	}
}
