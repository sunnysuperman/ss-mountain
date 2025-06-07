package com.sunnysuperman.mountain.lang.model;

import java.util.Map;

import com.sunnysuperman.mountain.lang.exception.service.ServiceException;

@SuppressWarnings("squid:S1452")
public class ApiResult<T> {

	private int errCode;

	private String errMsg;

	private Map<?, ?> errData;

	private T data;

	public ApiResult() {
		super();
	}

	public ApiResult(int errCode, T data) {
		this.errCode = errCode;
		this.data = data;
	}

	public boolean hasError() {
		return errCode != 0;
	}

	public ServiceException toServiceException() {
		return ServiceException.fromConcrete(errCode, errMsg, errData);
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Map<?, ?> getErrData() {
		return errData;
	}

	public void setErrData(Map<?, ?> errData) {
		this.errData = errData;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
