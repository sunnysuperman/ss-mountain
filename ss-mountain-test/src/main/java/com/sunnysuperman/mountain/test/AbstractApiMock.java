package com.sunnysuperman.mountain.test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.httpclient.HttpClient;
import com.sunnysuperman.mountain.httpclient.HttpTextResult;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.Jsons;

public abstract class AbstractApiMock implements ApiMock {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractApiMock.class);
	private static final byte[] LOCK = new byte[0];
	private static HttpClient client;
	private String responseBody;

	public final void log(String msg, Object... args) {
		LOG.info(msg, args);
	}

	private static HttpClient getClient() {
		if (client != null) {
			return client;
		}
		synchronized (LOCK) {
			if (client != null) {
				return client;
			}
			client = new HttpClient(10, 50).setReadTimeout(Integer.MAX_VALUE / 1000);
		}
		return client;
	}

	protected abstract String wrapUrl(String path);

	protected Map<String, Object> wrapHeaders() {
		return Collections.emptyMap();
	}

	private AbstractApiMock request(String method, String api, Map<String, Object> params,
			Map<String, Object> headers) {
		log(method + ": " + api);
		if (params != null) {
			for (Entry<String, Object> entry : params.entrySet()) {
				log(entry.getKey() + ": " + entry.getValue());
			}
		}
		if (params != null) {
			boolean uploadMode = params.values().stream().anyMatch(File.class::isInstance);
			if (uploadMode) {
				return doUpload(api, params, headers);
			}
		}
		String url = wrapUrl(api);
		HttpTextResult result;
		try {
			if (method.equals("GET")) {
				result = getClient().get(url, params, headers);
			} else {
				result = getClient().postForm(url, params, headers);
			}
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
		handleResult(result);
		return this;
	}

	private AbstractApiMock post(String api, Map<String, Object> params, Map<String, Object> headers) {
		return request("POST", api, params, headers);
	}

	private void handleResult(HttpTextResult result) {
		if (!result.ok()) {
			throw new UnexpectedException("Http error-code: " + result.getCode());
		}
		responseBody = result.getBody();
		log("response: \n" + responseBody);
	}

	private AbstractApiMock doUpload(String api, Map<String, Object> params, Map<String, Object> headers) {
		try {
			handleResult(getClient().postMultipart(wrapUrl(api), params, headers));
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}

	private Map<String, Object> paramsAsMap(Object[] paramsAsArray) {
		Map<String, Object> params = new HashMap<>();
		if (paramsAsArray != null && paramsAsArray.length > 0) {
			params = new HashMap<>(paramsAsArray.length / 2);
			for (int i = 0; i < paramsAsArray.length; i += 2) {
				Object value = paramsAsArray[i + 1];
				if (value == null) {
					continue;
				}
				params.put(paramsAsArray[i].toString(), value);
			}
		}
		return params;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends ApiMock> T get(String api, Object... params) {
		request("GET", api, paramsAsMap(params), wrapHeaders());
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends ApiMock> T post(String api, Object... params) {
		post(api, paramsAsMap(params), wrapHeaders());
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends ApiMock> T post(String api, Map<String, Object> params) {
		post(api, params, wrapHeaders());
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends ApiMock> T postJSON(String api, Object body) {
		String bodyAsString = body == null ? null : Jsons.write(body);
		String url = wrapUrl(api);
		log("POST: " + api + "\n" + bodyAsString);
		try {
			handleResult(getClient().postJSON(url, bodyAsString, wrapHeaders()));
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T extends ApiMock> T postJSON(String api) {
		postJSON(api, null);
		return (T) this;
	}

	@Override
	public final String getResponseBody() {
		return responseBody;
	}

}
