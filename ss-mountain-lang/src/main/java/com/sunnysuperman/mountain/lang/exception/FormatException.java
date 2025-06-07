package com.sunnysuperman.mountain.lang.exception;

public class FormatException extends RuntimeException {
	private static final long serialVersionUID = -1L;

	public FormatException(String message) {
		super(message);
	}

	public FormatException(String message, Throwable cause) {
		super(message, cause);
	}

}