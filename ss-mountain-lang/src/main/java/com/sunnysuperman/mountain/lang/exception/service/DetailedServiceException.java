package com.sunnysuperman.mountain.lang.exception.service;

public class DetailedServiceException extends ServiceException {
	private static final long serialVersionUID = -7140764920335762702L;

	public DetailedServiceException(String errorMsg) {
		super(GenericServiceError.DETAILED_MESSAGE, errorMsg);
	}
}
