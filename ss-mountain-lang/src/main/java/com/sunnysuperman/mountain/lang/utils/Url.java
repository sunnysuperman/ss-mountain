package com.sunnysuperman.mountain.lang.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public final class Url {

	public static class UrlComponents {
		private String url;
		private boolean valid;
		private String protocol;
		private String host;
		private String path;
		private String query;
		private Map<String, String> queryParams;
		private String hash;

		private UrlComponents(String url) {
			this.url = url;
			this.valid = init();
		}

		public static UrlComponents of(String url) {
			if (url == null || url.isEmpty()) {
				return null;
			}
			UrlComponents components = new UrlComponents(url);
			if (!components.valid) {
				return null;
			}
			return components;
		}

		private boolean init() {
			if (url == null) {
				return false;
			}
			final int protocolIndex = url.indexOf(":");
			if (protocolIndex < 0) {
				return false;
			}
			protocol = url.substring(0, protocolIndex);
			// http(s)://host/path?search#hash
			int hostIndex = protocolIndex + 3;
			int hashIndex = url.indexOf('#', hostIndex);
			int queryIndex = url.indexOf('?', hostIndex);
			if (hashIndex > 0 && queryIndex > hashIndex) {
				// http://xx.com#/abc?t=1
				// Hash后面的参数
				queryIndex = -1;
			}
			int pathIndex = url.indexOf('/', hostIndex);
			if (pathIndex > 0
					&& ((queryIndex >= 0 && pathIndex > queryIndex) || (hashIndex >= 0 && pathIndex > hashIndex))) {
				// 参数后面的路径 http://xx.com?t=/
				// Hash后面的路径 http://xx.com#/abc
				pathIndex = -1;
			}
			// 主机
			initHost(hostIndex, pathIndex, queryIndex, hashIndex);
			if (Str.isEmpty(host)) {
				return false;
			}
			// 路径
			initPath(pathIndex, queryIndex, hashIndex);
			// 参数
			initQuery(queryIndex, hashIndex);
			// hash
			initHash(hashIndex);
			return true;
		}

		private void initHost(int hostIndex, int pathIndex, int queryIndex, int hashIndex) {
			int hostEndIndex;
			if (pathIndex >= 0) {
				hostEndIndex = pathIndex;
			} else if (queryIndex >= 0) {
				hostEndIndex = queryIndex;
			} else if (hashIndex >= 0) {
				hostEndIndex = hashIndex;
			} else {
				hostEndIndex = -1;
			}
			if (hostEndIndex < 0) {
				host = url.substring(hostIndex);
			} else {
				host = url.substring(hostIndex, hostEndIndex);
			}
		}

		private void initPath(int pathIndex, int queryIndex, int hashIndex) {
			if (pathIndex >= 0) {
				int pathEndIndex;
				if (queryIndex >= 0) {
					pathEndIndex = queryIndex;
				} else if (hashIndex >= 0) {
					pathEndIndex = hashIndex;
				} else {
					pathEndIndex = -1;
				}
				if (pathEndIndex < 0) {
					path = url.substring(pathIndex);
				} else {
					path = url.substring(pathIndex, pathEndIndex);
				}
			}
			if (Str.isEmpty(path)) {
				path = "/";
			}
		}

		private void initQuery(int queryIndex, int hashIndex) {
			if (queryIndex >= 0) {
				if (hashIndex < 0) {
					// http://xx.com/abc?key=value
					query = url.substring(queryIndex + 1);
				} else {
					// http://xx.com/abc?key=value#/def
					query = url.substring(queryIndex + 1, hashIndex);
				}
			}
		}

		private void initHash(int hashIndex) {
			if (hashIndex >= 0) {
				hash = url.substring(hashIndex + 1);
			}
		}

		public String getProtocol() {
			return protocol;
		}

		public String getHost() {
			return host;
		}

		public String getPath() {
			return path;
		}

		public String getQuery() {
			return query;
		}

		public String getHash() {
			return hash;
		}

		public Map<String, String> getQueryParams() {
			if (queryParams == null) {
				queryParams = getParamsByQuery(query);
			}
			return queryParams;
		}

	}

	public static class CanonicalUrlBuilder {
		private String protocol;
		private String host;
		private String path;
		private String query;
		private String hash;
		private Map<String, String> queryParams;

		public CanonicalUrlBuilder() {
			super();
		}

		public CanonicalUrlBuilder(UrlComponents components) {
			if (components == null || !components.valid) {
				throw new IllegalArgumentException("Bad url");
			}
			protocol = components.protocol;
			host = components.host;
			path = components.path;
			query = components.query;
			hash = components.hash;
			queryParams = components.queryParams;
		}

		public String build() {
			StringBuilder buf = new StringBuilder(require(protocol)).append("://").append(require(host))
					.append(require(path));
			if (Str.isNotEmpty(query)) {
				buf.append('?').append(query);
			} else if (queryParams != null && !queryParams.isEmpty()) {
				buf.append('?');
				boolean first = true;
				for (Entry<String, String> entry : queryParams.entrySet()) {
					if (!first) {
						buf.append('&');
					}
					buf.append(require(entry.getKey())).append('=').append(Str.nullToEmpty(entry.getValue()));
					first = false;
				}
			}
			if (Str.isNotEmpty(hash)) {
				buf.append('#');
				buf.append(hash);
			}
			return buf.toString();
		}

		public String getProtocol() {
			return protocol;
		}

		public CanonicalUrlBuilder setProtocol(String protocol) {
			this.protocol = protocol;
			return this;
		}

		public String getHost() {
			return host;
		}

		public CanonicalUrlBuilder setHost(String host) {
			this.host = host;
			return this;
		}

		public String getPath() {
			return path;
		}

		public CanonicalUrlBuilder setPath(String path) {
			this.path = path;
			return this;
		}

		public CanonicalUrlBuilder setQuery(String query) {
			this.queryParams = getParamsByQuery(query);
			this.query = null;
			return this;
		}

		public String getHash() {
			return hash;
		}

		public CanonicalUrlBuilder setHash(String hash) {
			if (hash != null && hash.startsWith("#")) {
				hash = hash.substring(1);
			}
			this.hash = hash;
			return this;
		}

		public Map<String, String> getQueryParams() {
			return queryParams;
		}

		public CanonicalUrlBuilder setQueryParams(Map<String, String> queryParams) {
			this.queryParams = queryParams;
			this.query = null;
			return this;
		}

		private String require(String s) {
			if (Str.isEmpty(s)) {
				throw new NullPointerException("require component");
			}
			return s;
		}

	}

	private Url() {
	}

	/** 是否有效http链接 **/
	public static boolean isValidHttpUrl(String url) {
		if (url == null) {
			return false;
		}
		return url.startsWith("http://") || url.startsWith("https://");
	}

	/** URL编码 **/
	public static String decode(String url) {
		if (Str.isEmpty(url)) {
			return null;
		}
		return URLDecoder.decode(url, StandardCharsets.UTF_8);
	}

	/** URL解码 **/
	public static String encode(String url) {
		if (Str.isEmpty(url)) {
			return null;
		}
		return URLEncoder.encode(url, StandardCharsets.UTF_8);
	}

	/** 获取链接各字段(如果返回空，说明为无效链接) **/
	public static UrlComponents getComponents(String url) {
		return UrlComponents.of(url);
	}

	/** 获取链接的主机地址(含端口) **/
	public static String getHost(String url) {
		UrlComponents components = getComponents(url);
		return components == null ? null : components.getHost();
	}

	/** 获取链接的路径 **/
	public static String getPath(String url) {
		UrlComponents components = getComponents(url);
		return components == null ? null : components.getPath();
	}

	/** 替换链接主机地址 **/
	public static String replaceHost(String url, String host) {
		UrlComponents components = getComponents(url);
		if (components == null) {
			return url;
		}
		return new CanonicalUrlBuilder(components).setHost(host).build();
	}

	/** 获取链接参数（注意不是传完整的URL，传的是?号后面的参数部分） **/
	public static Map<String, String> getParamsByQuery(String query) {
		if (query != null && !query.isEmpty() && query.charAt(0) == '?') {
			query = query.substring(1);
		}
		if (query == null || query.isEmpty()) {
			return Collections.emptyMap();
		}
		String[] pairs = query.split("&");
		if (Arrays.isEmpty(pairs)) {
			return Collections.emptyMap();
		}
		Map<String, String> kv = new HashMap<>(pairs.length);
		for (String pair : pairs) {
			String[] tokens = pair.split("=");
			String key = tokens[0];
			if (key.isEmpty()) {
				continue;
			}
			String value = tokens.length > 1 ? tokens[1] : null;
			if (value != null) {
				value = URLDecoder.decode(value, StandardCharsets.UTF_8);
			} else {
				value = Str.EMPTY;
			}
			kv.put(key, value);
		}
		return kv;
	}

	/** 添加链接参数 **/
	public static String appendParams(String url, Map<String, ?> params) {
		if (params == null || params.isEmpty()) {
			return url;
		}
		UrlComponents components = getComponents(url);
		if (components == null) {
			return url;
		}
		Map<String, String> mergedParams = new HashMap<>();
		if (components.getQueryParams() != null) {
			mergedParams.putAll(components.getQueryParams());
		}
		params.entrySet().forEach(e -> mergedParams.put(e.getKey(), Str.parse(e.getValue())));
		return new CanonicalUrlBuilder(components).setQueryParams(mergedParams).build();
	}

	/** 删除链接参数 **/
	public static String deleteParams(String url, Collection<String> keys) {
		if (keys == null || keys.isEmpty()) {
			return url;
		}
		UrlComponents components = getComponents(url);
		if (components == null) {
			return url;
		}
		Map<String, String> params = components.getQueryParams();
		if (params == null || params.isEmpty()) {
			return url;
		}
		boolean containsAny = keys.stream().anyMatch(params::containsKey);
		if (!containsAny) {
			return url;
		}
		Map<String, String> newParams = params.entrySet().stream().filter(i -> !keys.contains(i.getKey()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		return new CanonicalUrlBuilder(components).setQueryParams(newParams).build();
	}

	/** 拼接链接路径，如/abc, def 拼接后为 /abc/def **/
	public static String appendPath(String... components) {
		StringBuilder buf = new StringBuilder(components[0]);
		for (int i = 1; i < components.length; i++) {
			String component = components[i];
			if (Str.isEmpty(component)) {
				continue;
			}
			if (component.charAt(0) == '/' && buf.charAt(buf.length() - 1) == '/') {
				buf.append(component, 1, component.length());
			} else {
				buf.append(component);
			}
		}
		return buf.toString();
	}

}
