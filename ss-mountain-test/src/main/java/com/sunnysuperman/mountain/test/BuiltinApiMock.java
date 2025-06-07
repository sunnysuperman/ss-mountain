package com.sunnysuperman.mountain.test;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.exception.service.ServiceException;
import com.sunnysuperman.mountain.lang.model.ApiResult;
import com.sunnysuperman.mountain.lang.pagination.Page;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;

public abstract class BuiltinApiMock extends AbstractApiMock {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends ApiMock> T assertOK() {
		int errorCode = getErrorCode();
		if (errorCode != 0) {
			throw ServiceException.fromConcrete(errorCode, getErrorMsg(), null);
		}
		return (T) this;
	}

	public int getErrorCode() {
		Map<String, Object> ret = Jsons.readForMap(getResponseBody());
		return Num.parseInteger(ret.get("errCode")).intValue();
	}

	public String getErrorMsg() {
		Map<String, Object> ret = Jsons.readForMap(getResponseBody());
		return Str.parse(ret.get("errMsg"));
	}

	public BuiltinApiMock assertError() {
		int realErrorCode = getErrorCode();
		if (realErrorCode <= 0) {
			throw new UnexpectedException("assert errorCode > 0, but real error code is " + realErrorCode);
		}
		return this;
	}

	public BuiltinApiMock assertError(int errorCode) {
		int realErrorCode = getErrorCode();
		if (realErrorCode != errorCode) {
			throw new UnexpectedException(
					"assert errorCode " + errorCode + ", but real error code is " + realErrorCode);
		}
		return this;
	}

	public BuiltinApiMock assertErrorMsg(String errorMsg) {
		String actualErrorMsg = getErrorMsg();
		if (!errorMsg.equals(actualErrorMsg)) {
			throw new UnexpectedException(
					"assert error message: " + errorMsg + ", but actual error message: " + actualErrorMsg);
		}
		return this;
	}

	public BuiltinApiMock assertError(int errorCode, String errorMsg) {
		assertError(errorCode);
		if (errorMsg != null) {
			assertErrorMsg(errorMsg);
		}
		return this;
	}

	public BuiltinApiMock asserts(boolean ok) {
		if (ok) {
			assertOK();
		} else {
			assertError();
		}
		return this;
	}

	private <T> T readResult(Class<T> type) {
		ApiResult<T> result = Jsons.readForParametricType(getResponseBody(), ApiResult.class,
				TypeFactory.defaultInstance().constructType(type));
		return getData(result);
	}

	private <T> T getData(ApiResult<T> result) {
		if (result.getErrCode() != 0) {
			throw ServiceException.fromConcrete(result.getErrCode(), result.getErrMsg(), null);
		}
		return result.getData();
	}

	public <T> T resultForObject(Class<T> type) {
		return readResult(type);
	}

	public String resultForString() {
		return readResult(String.class);
	}

	public Integer resultForInteger() {
		return readResult(Integer.class);
	}

	public Long resultForLong() {
		return readResult(Long.class);
	}

	public boolean resultForBoolean() {
		return readResult(Boolean.class);
	}

	public <T> List<T> resultForList(Class<T> type) {
		ApiResult<List<T>> result = Jsons.readForParametricType(getResponseBody(), ApiResult.class,
				TypeFactory.defaultInstance().constructParametricType(List.class, type));
		return getData(result);
	}

	public <T> Page<T> resultForPage(Class<T> type) {
		ApiResult<Page<T>> result = Jsons.readForParametricType(getResponseBody(), ApiResult.class,
				TypeFactory.defaultInstance().constructParametricType(Page.class, type));
		return getData(result);
	}

	public <T> PullPage<T> resultForPullPage(Class<T> type) {
		ApiResult<PullPage<T>> result = Jsons.readForParametricType(getResponseBody(), ApiResult.class,
				TypeFactory.defaultInstance().constructParametricType(PullPage.class, type));
		return getData(result);
	}

}
