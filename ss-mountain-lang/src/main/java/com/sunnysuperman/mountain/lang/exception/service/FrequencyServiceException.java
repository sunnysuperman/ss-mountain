package com.sunnysuperman.mountain.lang.exception.service;

public class FrequencyServiceException extends ServiceException {
	private static final long serialVersionUID = 7404151147635683478L;

	public FrequencyServiceException() {
		super(GenericServiceError.OPERATION_TOO_FREQUENT);
	}
}
