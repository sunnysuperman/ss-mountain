package com.sunnysuperman.mountain.lang.exception.service;

import java.util.Collections;

public class PermissionDeniedServiceException extends ServiceException {
	private static final long serialVersionUID = 686137446373873065L;

	public PermissionDeniedServiceException() {
		super(GenericServiceError.PERMISSION_DENIED);
	}

	public PermissionDeniedServiceException(String msg) {
		super(Collections.singletonMap("detail", msg), GenericServiceError.PERMISSION_DENIED);
	}
}
