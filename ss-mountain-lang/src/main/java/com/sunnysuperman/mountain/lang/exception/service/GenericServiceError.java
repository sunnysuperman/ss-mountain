package com.sunnysuperman.mountain.lang.exception.service;

public enum GenericServiceError implements ServiceErrorCode {

	UNKNOWN_ERROR(1),

	ILLEGAL_ARGUMENT(2),

	PERMISSION_DENIED(3),

	DETAILED_MESSAGE(4),

	SESSION_EXPIRES(5),

	OPERATION_TOO_FREQUENT(6),

	DATA_NOT_FOUND(7),

	NEED_TO_UPGRADE(8);

	private int code;

	private GenericServiceError(int code) {
		this.code = code;
	}

	@Override
	public int code() {
		return code;
	}

}
