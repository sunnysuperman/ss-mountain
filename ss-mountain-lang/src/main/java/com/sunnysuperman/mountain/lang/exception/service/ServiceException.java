package com.sunnysuperman.mountain.lang.exception.service;

import java.util.Map;

import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.Str;

@SuppressWarnings("serial")
public class ServiceException extends RuntimeException {
	private final String errorMsg;
	private final int errorCode;
	private final transient Object[] errorParams;
	private final transient Map<?, ?> errorData;

	private ServiceException(String errorMsg, Map<?, ?> errorData, int errorCode, Object... errorParams) {
		super(errorMsg);
		this.errorMsg = errorMsg;
		this.errorCode = errorCode;
		this.errorParams = errorParams;
		this.errorData = errorData;
	}

	public ServiceException(int errorCode, Object... errorParams) {
		this(null, null, errorCode, errorParams);
	}

	public ServiceException(Map<?, ?> errorData, int errorCode, Object... errorParams) {
		this(null, errorData, errorCode, errorParams);
	}

	public ServiceException(ServiceErrorCode errorCode, Object... errorParams) {
		this(null, null, errorCode.code(), errorParams);
	}

	public ServiceException(Map<?, ?> errorData, ServiceErrorCode errorCode, Object... errorParams) {
		this(null, errorData, errorCode.code(), errorParams);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public Object[] getErrorParams() {
		return errorParams;
	}

	@SuppressWarnings("rawtypes")
	public Map getErrorData() {
		return errorData;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	@Override
	public String getMessage() {
		String msg = super.getMessage();
		if (msg == null) {
			StringBuilder msgBuf = new StringBuilder("error-code:").append(errorCode);
			if (errorParams != null && errorParams.length > 0) {
				msgBuf.append(", error-params:").append(Str.join(errorParams));
			}
			if (errorData != null) {
				msgBuf.append(", error-data:").append(Jsons.write(errorData));
			}
			msg = msgBuf.toString();
		}
		return msg;
	}

	public static ServiceException fromConcrete(int errorCode, String errorMsg, Map<?, ?> errorData) {
		return new ServiceException(errorMsg, errorData, errorCode);
	}

}
