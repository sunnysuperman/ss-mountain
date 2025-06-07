package com.sunnysuperman.mountain.httpclient;

import java.util.Map;

public class HttpDownloadOptions {
	private Map<String, Object> params;
	private Map<String, Object> headers;
	private long maxSize = -1;
	private boolean retrieveResponseHeaders;
	private Map<String, String> responseHeaders;

	public Map<String, Object> getParams() {
		return params;
	}

	public HttpDownloadOptions setParams(Map<String, Object> params) {
		this.params = params;
		return this;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public HttpDownloadOptions setHeaders(Map<String, Object> headers) {
		this.headers = headers;
		return this;
	}

	public long getMaxSize() {
		return maxSize;
	}

	public HttpDownloadOptions setMaxSize(long maxSize) {
		this.maxSize = maxSize;
		return this;
	}

	public boolean isRetrieveResponseHeaders() {
		return retrieveResponseHeaders;
	}

	public HttpDownloadOptions setRetrieveResponseHeaders(boolean retrieveResponseHeaders) {
		this.retrieveResponseHeaders = retrieveResponseHeaders;
		return this;
	}

	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	public HttpDownloadOptions setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
		return this;
	}

}