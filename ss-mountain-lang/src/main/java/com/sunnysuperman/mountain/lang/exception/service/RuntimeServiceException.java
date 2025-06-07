package com.sunnysuperman.mountain.lang.exception.service;

public class RuntimeServiceException extends ServiceException {
	private static final long serialVersionUID = 550893169395750576L;

	public RuntimeServiceException() {
		super(GenericServiceError.UNKNOWN_ERROR);
	}

	public RuntimeServiceException(Throwable t) {
		this();
		this.initCause(t);
	}

	public RuntimeServiceException(String s) {
		this();
		this.initCause(new RuntimeException(s));
	}
}
