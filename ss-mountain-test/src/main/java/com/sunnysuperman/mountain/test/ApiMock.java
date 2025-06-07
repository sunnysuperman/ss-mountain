package com.sunnysuperman.mountain.test;

import java.util.Map;

public interface ApiMock {

	<T extends ApiMock> T get(String api, Object... params);

	<T extends ApiMock> T post(String api, Object... params);

	<T extends ApiMock> T post(String api, Map<String, Object> params);

	<T extends ApiMock> T postJSON(String api, Object body);

	<T extends ApiMock> T postJSON(String api);

	String getResponseBody();

	<T extends ApiMock> T assertOK();

}
