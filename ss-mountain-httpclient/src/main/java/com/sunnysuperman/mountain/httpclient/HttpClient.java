package com.sunnysuperman.mountain.httpclient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.IOUtil;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;

import okhttp3.Authenticator;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 
 * Thread-safe http client
 * 
 **/
public class HttpClient {
	private static final HttpDownloadOptions DEFAULT_DOWNLOAD_OPTIONS = new HttpDownloadOptions();
	private static final RequestOptions DEFAULT_REQUEST_OPTIONS = new RequestOptions();
	private static final byte[] EMPTY = new byte[0];
	private static final String CONTENT_TYPE_JSON = "application/json";

	private int connectTimeout = 15;
	private int readTimeout = 20;
	private boolean followRedirects = true;
	private ConnectionPoolHolder connectionPool;
	private Proxy proxy;
	private Authenticator proxyAuthenticator;
	private OkHttpClient client;
	private volatile boolean destroyed;

	public HttpClient(int maxIdleConnections, long keepAliveDuration) {
		super();
		this.connectionPool = new ConnectionPoolHolder(maxIdleConnections, keepAliveDuration);
	}

	public HttpClient(int maxIdleConnections) {
		this(maxIdleConnections, 50);
	}

	public HttpClient(ConnectionPoolHolder connectionPool) {
		super();
		this.connectionPool = Objects.requireNonNull(connectionPool);
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public HttpClient setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public HttpClient setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public boolean isFollowRedirects() {
		return followRedirects;
	}

	public HttpClient setFollowRedirects(boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}

	public ConnectionPoolHolder getConnectionPool() {
		return connectionPool;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public HttpClient setProxy(Proxy proxy) {
		this.proxy = proxy;
		return this;
	}

	public Authenticator getProxyAuthenticator() {
		return proxyAuthenticator;
	}

	public void setProxyAuthenticator(Authenticator proxyAuthenticator) {
		this.proxyAuthenticator = proxyAuthenticator;
	}

	public void destroy() {
		synchronized (this) {
			if (client != null) {
				client.connectionPool().evictAll();
				destroyed = true;
			}
		}
	}

	public HttpTextResult get(String url, Map<String, ?> params, RequestOptions options) throws IOException {
		options = ensureRequestOptions(options);
		Request request = getRequestBuilder(url, params, options.getHeaders()).build();
		return executeAndGetTextResult(request, options);
	}

	public HttpTextResult get(String url, Map<String, ?> params, Map<String, Object> headers) throws IOException {
		return get(url, params, headersAsOptions(headers));
	}

	public HttpTextResult get(String url, Map<String, ?> params) throws IOException {
		return get(url, params, (RequestOptions) null);
	}

	public HttpTextResult get(String url) throws IOException {
		return get(url, null, (RequestOptions) null);
	}

	public HttpTextResult post(String url, String mediaType, String body, RequestOptions options) throws IOException {
		options = ensureRequestOptions(options);
		Request.Builder builder = getRequestBuilder(url, null, options.getHeaders());

		RequestBody requestBody = body == null ? RequestBody.create(null, EMPTY)
				: RequestBody.create(MediaType.parse(mediaType), body);

		Request request = builder.post(requestBody).build();
		return executeAndGetTextResult(request, options);
	}

	public HttpTextResult post(String url, String mediaType, String body) throws IOException {
		return post(url, mediaType, body, (RequestOptions) null);
	}

	public HttpTextResult post(String url, String mediaType, String body, Map<String, Object> headers)
			throws IOException {
		return post(url, mediaType, body, headersAsOptions(headers));
	}

	public HttpTextResult postForm(String url, Map<String, ?> formData, RequestOptions options) throws IOException {
		options = ensureRequestOptions(options);
		Request.Builder builder = getRequestBuilder(url, null, options.getHeaders());

		FormBody.Builder bodyBuilder = new FormBody.Builder();
		if (formData != null && !formData.isEmpty()) {
			for (Entry<String, ?> entry : formData.entrySet()) {
				bodyBuilder.add(entry.getKey(), Str.parse(entry.getValue(), Str.EMPTY));
			}
		}

		Request request = builder.post(bodyBuilder.build()).build();
		return executeAndGetTextResult(request, options);
	}

	public HttpTextResult postForm(String url, Map<String, ?> formData, Map<String, Object> headers)
			throws IOException {
		return postForm(url, formData, headersAsOptions(headers));
	}

	public HttpTextResult postForm(String url, Map<String, ?> formData) throws IOException {
		return postForm(url, formData, (RequestOptions) null);
	}

	public HttpTextResult postJSON(String url, String body, RequestOptions options) throws IOException {
		return post(url, CONTENT_TYPE_JSON, body, options);
	}

	public HttpTextResult postJSON(String url, String body, Map<String, Object> headers) throws IOException {
		return post(url, CONTENT_TYPE_JSON, body, headers);
	}

	public HttpTextResult postJSON(String url, String body) throws IOException {
		return post(url, CONTENT_TYPE_JSON, body, (RequestOptions) null);
	}

	public HttpTextResult postMultipart(String url, Map<String, ?> body, RequestOptions options) throws IOException {
		options = ensureRequestOptions(options);
		Request.Builder builder = getRequestBuilder(url, null, options.getHeaders());

		MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		for (Entry<String, ?> entry : body.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value == null) {
				value = Str.EMPTY;
			}
			if (value instanceof File) {
				File file = (File) value;
				String fileName = file.getName();
				String contentType = "application/octet-stream";
				RequestBody data = RequestBody.create(MediaType.parse(contentType), file);
				bodyBuilder.addFormDataPart(key, fileName, data);
			} else if (value instanceof HttpUploadFile) {
				HttpUploadFile file = (HttpUploadFile) value;
				String fileName = file.getFileName() != null ? file.getFileName() : file.getFile().getName();
				String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
				RequestBody data = RequestBody.create(MediaType.parse(contentType), file.getFile());
				bodyBuilder.addFormDataPart(key, fileName, data);
			} else {
				bodyBuilder.addFormDataPart(key, Str.parse(value));
			}
		}
		Request request = builder.post(bodyBuilder.build()).build();
		return executeAndGetTextResult(request, options);
	}

	public HttpTextResult postMultipart(String url, Map<String, ?> body, Map<String, Object> headers)
			throws IOException {
		return postMultipart(url, body, headersAsOptions(headers));
	}

	public HttpTextResult postMultipart(String url, Map<String, ?> body) throws IOException {
		return postMultipart(url, body, (RequestOptions) null);
	}

	public boolean download(String url, OutputStream out) throws IOException {
		return download(url, out, null);
	}

	public boolean download(String url, OutputStream out, HttpDownloadOptions options) throws IOException {
		if (options == null) {
			options = DEFAULT_DOWNLOAD_OPTIONS;
		}
		Request request = getRequestBuilder(url, options.getParams(), options.getHeaders()).build();
		Response response = null;
		InputStream in = null;
		try {
			response = execute(request);
			if (options.getMaxSize() > 0) {
				long length = Num.parseLongValue(response.header("Content-Length"), -1);
				if (length < 0 || length > options.getMaxSize()) {
					return false;
				}
			}
			if (options.isRetrieveResponseHeaders()) {
				Map<String, List<String>> headers = response.headers().toMultimap();
				Map<String, String> responseHeaders = new HashMap<>();
				for (Entry<String, List<String>> entry : headers.entrySet()) {
					responseHeaders.put(entry.getKey(), entry.getValue().get(0));
				}
				options.setResponseHeaders(responseHeaders);
			}
			in = response.body().byteStream();
			IOUtil.copy(in, out);
			if (!response.isSuccessful()) {
				throw new IOException("Failed to download: " + url);
			}
			return true;
		} finally {
			IOUtil.close(in);
			IOUtil.close(out);
			IOUtil.close(response);
		}
	}

	public long getContentLength(String url, Map<String, ?> params, Map<String, ?> headers) throws IOException {
		Request request = getRequestBuilder(url, params, headers).head().build();
		Response response = null;
		try {
			response = execute(request);
			return response.body().contentLength();
		} finally {
			IOUtil.close(response);
		}
	}

	public int idleConnectionCount() {
		return connectionPool.get().idleConnectionCount();
	}

	public int connectionCount() {
		return connectionPool.get().connectionCount();
	}

	private OkHttpClient createClient() {
		OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(connectTimeout, TimeUnit.SECONDS)
				.readTimeout(readTimeout, TimeUnit.SECONDS);
		if (proxy != null) {
			builder.proxy(proxy);
		}
		if (proxyAuthenticator != null) {
			builder.proxyAuthenticator(proxyAuthenticator);
		}
		builder.connectionPool(connectionPool.get());
		builder.followRedirects(followRedirects);
		return builder.build();
	}

	private OkHttpClient getClient() {
		OkHttpClient localClient = client;
		if (localClient == null) {
			synchronized (this) {
				localClient = client;
				if (localClient == null) {
					localClient = client = createClient();
				}
			}
		}
		return localClient;
	}

	private RequestOptions headersAsOptions(Map<String, Object> headers) {
		return headers == null ? null : new RequestOptions().setHeaders(headers);
	}

	private RequestOptions ensureRequestOptions(RequestOptions options) {
		return options == null ? DEFAULT_REQUEST_OPTIONS : options;
	}

	private String makeUrl(String url, Map<String, ?> params) {
		if (params == null || params.isEmpty()) {
			return url;
		}
		StringBuilder urlBuf = new StringBuilder(url);
		boolean appendQuestionMark = url.indexOf('?') < 0;
		for (Entry<String, ?> entry : params.entrySet()) {
			if (appendQuestionMark) {
				urlBuf.append('?');
				appendQuestionMark = false;
			} else {
				urlBuf.append('&');
			}
			urlBuf.append(entry.getKey()).append('=');
			if (entry.getValue() != null) {
				try {
					urlBuf.append(URLEncoder.encode(entry.getValue().toString(), Str.UTF8));
				} catch (UnsupportedEncodingException e) {
					throw new UnexpectedException(e);
				}
			}
		}
		return urlBuf.toString();
	}

	private Request.Builder getRequestBuilder(String url, Map<String, ?> params, Map<String, ?> headers) {
		Request.Builder builder = new Request.Builder().url(makeUrl(url, params));
		if (headers != null && !headers.isEmpty()) {
			for (Entry<String, ?> entry : headers.entrySet()) {
				builder.addHeader(entry.getKey(), Str.parse(entry.getValue(), Str.EMPTY));
			}
		}
		return builder;
	}

	private Response execute(Request request) throws IOException {
		if (destroyed) {
			throw new IOException("HttpClient already destroyed");
		}
		return getClient().newCall(request).execute();
	}

	private HttpTextResult executeAndGetTextResult(Request request, RequestOptions options) throws IOException {
		Response response = null;
		try {
			response = execute(request);
			return new HttpTextResult(response.code(), response.body().string(), response.headers(),
					options.isRetainPriorResponseHeaders() && response.priorResponse() != null
							? response.priorResponse().headers()
							: null);
		} finally {
			IOUtil.close(response);
		}
	}
}
