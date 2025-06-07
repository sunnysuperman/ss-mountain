package com.sunnysuperman.mountain.httpclient;

import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class HttpTextResult {
	private int code;
	private String body;
	private Headers headers;
	private Headers priorHeaders;
	private Map<String, List<String>> headerMap;
	private Map<String, List<String>> priorHeaderMap;

	public HttpTextResult(int code, String body, Headers headers, Headers priorHeaders) {
		super();
		this.code = code;
		this.body = body;
		this.headers = headers;
		this.priorHeaders = priorHeaders;
	}

	public boolean ok() {
		return code == 200;
	}

	public String getHeader(String name) {
		return headers == null ? null : headers.get(name);
	}

	public String getPriorHeader(String name) {
		return priorHeaders == null ? null : priorHeaders.get(name);
	}

	public Map<String, List<String>> getHeaders() {
		if (headerMap == null) {
			headerMap = headers == null ? null : headers.toMultimap();
		}
		return headerMap;
	}

	public Map<String, List<String>> getPriorHeaders() {
		if (priorHeaderMap == null) {
			priorHeaderMap = priorHeaders == null ? null : priorHeaders.toMultimap();
		}
		return priorHeaderMap;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}