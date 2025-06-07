package com.sunnysuperman.mountain.lang.exception.service;

import java.util.Collections;

public class DataNotFoundServiceException extends ServiceException {
	private static final long serialVersionUID = 7404151147635683478L;

	public DataNotFoundServiceException() {
		super(GenericServiceError.DATA_NOT_FOUND);
	}

	public DataNotFoundServiceException(String message) {
		super(Collections.singletonMap("message", message), GenericServiceError.DATA_NOT_FOUND);
	}
}
