package com.sunnysuperman.mountain.lang.exception.service;

public class SessionServiceException extends ServiceException {
	private static final long serialVersionUID = 7404151147635683478L;

	public SessionServiceException() {
		super(GenericServiceError.SESSION_EXPIRES);
	}
}
