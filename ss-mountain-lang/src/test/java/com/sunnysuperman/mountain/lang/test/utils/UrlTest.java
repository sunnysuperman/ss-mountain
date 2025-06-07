package com.sunnysuperman.mountain.lang.test.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import com.sunnysuperman.mountain.lang.utils.Url;
import com.sunnysuperman.mountain.lang.utils.Url.UrlComponents;

class UrlTest {

	@Test
	void testGetComponents() {
		assertComponents(Url.getComponents("http://xx.com"), "xx.com", "/", null);
		assertComponents(Url.getComponents("http://xx.com/"), "xx.com", "/", null);
		assertComponents(Url.getComponents("http://xx.com?"), "xx.com", "/", null);
		assertComponents(Url.getComponents("http://xx.com?/abc"), "xx.com", "/", Collections.singletonMap("/abc", ""));
		assertComponents(Url.getComponents("http://xx.com?#"), "xx.com", "/", null, "");
		assertComponents(Url.getComponents("http://xx.com?#/abc"), "xx.com", "/", null, "/abc");
		assertComponents(Url.getComponents("http://xx.com/abc"), "xx.com", "/abc", null);
		assertComponents(Url.getComponents("http://xx.com/abc?"), "xx.com", "/abc", null);
		assertComponents(Url.getComponents("http://xx.com/abc?#"), "xx.com", "/abc", null, "");
		assertComponents(Url.getComponents("http://xx.com/abc#"), "xx.com", "/abc", null, "");
		assertComponents(Url.getComponents("http://xx.com/abc#?"), "xx.com", "/abc", null, "?");

		assertComponents(Url.getComponents("http://xx.com/abc?key=value"), "xx.com", "/abc", stringMap("key", "value"));
		assertComponents(Url.getComponents("http://xx.com/abc?key=value&key2=value2"), "xx.com", "/abc",
				stringMap("key", "value", "key2", "value2"));
		assertComponents(Url.getComponents("http://xx.com/abc?key=value#"), "xx.com", "/abc", stringMap("key", "value"),
				"");
		assertComponents(Url.getComponents("http://xx.com/abc?key=value#/def"), "xx.com", "/abc",
				stringMap("key", "value"), "/def");
		assertComponents(Url.getComponents("http://xx.com/abc?key=value#/def?"), "xx.com", "/abc",
				stringMap("key", "value"), "/def?");

		assertComponents(Url.getComponents("http://xx.com#/def"), "xx.com", "/", null, "/def");
		assertComponents(Url.getComponents("http://xx.com/#/def"), "xx.com", "/", null, "/def");
		assertComponents(Url.getComponents("http://xx.com/abc#/def"), "xx.com", "/abc", null, "/def");

		assertComponents(Url.getComponents("http://xx.com#/def?h=i"), "xx.com", "/", null, "/def?h=i");
		assertComponents(Url.getComponents("http://xx.com/#/def?h=i"), "xx.com", "/", null, "/def?h=i");
		assertComponents(Url.getComponents("http://xx.com/abc#/def?h=i"), "xx.com", "/abc", null, "/def?h=i");
		assertComponents(Url.getComponents("http://xx.com/abc#/?h=i"), "xx.com", "/abc", null, "/?h=i");
		assertComponents(Url.getComponents("http://xx.com/abc?key=value#/def?h=i"), "xx.com", "/abc",
				stringMap("key", "value"), "/def?h=i");
		assertComponents(Url.getComponents("http://xx.com/abc?key=value#/def?h=i&j=k"), "xx.com", "/abc",
				stringMap("key", "value"), "/def?h=i&j=k");
	}

	@Test
	void testGetQuery() {
		assertEquals("key=value", Url.getComponents("http://xx.com/abc?key=value").getQuery());
		assertEquals("key=value&key2=value2", Url.getComponents("http://xx.com/abc?key=value&key2=value2").getQuery());
	}

	@Test
	void testGetHost() {
		assertNull(Url.getHost(null));
		assertNull(Url.getHost(""));
		assertEquals("www.test.com", Url.getHost("https://www.test.com"));
		assertEquals("www.test.com", Url.getHost("https://www.test.com"));
		assertEquals("www.test.com", Url.getHost("https://www.test.com/"));
		assertEquals("www.test.com", Url.getHost("https://www.test.com#/"));
		assertEquals("www.test.com", Url.getHost("https://www.test.com?abc/"));
		assertEquals("www.test.com", Url.getHost("https://www.test.com?#/"));
	}

	@Test
	void testRelaceHost() {
		assertNull(Url.replaceHost(null, "test.com"));
		assertEquals("https://test.com/", Url.replaceHost("https://www.baidu.com", "test.com"));
		assertEquals("https://test.com/?sign=123", Url.replaceHost("https://www.baidu.com?sign=123", "test.com"));
		assertEquals("https://test.com/?sign=123&key=567#/abc",
				Url.replaceHost("https://www.baidu.com?sign=123&key=567#/abc", "test.com"));
	}

	@Test
	void testGetPath() {
		assertNull(Url.getPath(null));
		assertNull(Url.getPath(""));
		assertEquals("/", Url.getPath("https://www.test.com"));
		assertEquals("/", Url.getPath("https://www.test.com/"));
		assertEquals("/abc", Url.getPath("https://www.test.com/abc"));
		assertEquals("/abc", Url.getPath("https://www.test.com/abc?id=123"));
		assertEquals("/abc", Url.getPath("https://www.test.com/abc?id=123#/def"));
	}

	@Test
	void testAppendParams() {
		assertEquals("http://xx.com", Url.appendParams("http://xx.com", Map.of()));
		assertEquals("http://xx.com/?code=123", Url.appendParams("http://xx.com", Map.of("code", "123")));
		assertEquals("http://xx.com/?code=123", Url.appendParams("http://xx.com?", Map.of("code", "123")));
		assertEquals("http://xx.com/?w=100&code=123", Url.appendParams("http://xx.com?w=100", Map.of("code", "123")));
		assertEquals("http://xx.com/?code=123&w=100",
				Url.appendParams("http://xx.com/?code=567&w=100", Map.of("code", "123")));
		assertEquals("http://xx.com/?h=500&code=123&w=100",
				Url.appendParams("http://xx.com/?code=567&w=100", Map.of("code", "123", "h", "500")));
	}

	@Test
	void testDeleteParams() {
		List<String> keys = List.of("appId", "appid", "code");

		assertEquals("http://xx.com", Url.deleteParams("http://xx.com", keys));
		assertEquals("http://xx.com/", Url.deleteParams("http://xx.com/", keys));
		assertEquals("http://xx.com?", Url.deleteParams("http://xx.com?", keys));
		assertEquals("http://xx.com?w=100", Url.deleteParams("http://xx.com?w=100", keys));
		assertEquals("http://xx.com/?w=100", Url.deleteParams("http://xx.com/?w=100", keys));
		assertEquals("http://xx.com/", Url.deleteParams("http://xx.com?appid=100", keys));
		assertEquals("http://xx.com/", Url.deleteParams("http://xx.com?appid=abc&appId=100", keys));
		assertEquals("http://xx.com/", Url.deleteParams("http://xx.com?appid=abc&appId=100&code=def", keys));
	}

	private Map<String, String> stringMap(String... t) {
		Map<String, String> params = new HashMap<>(t.length);
		for (int i = 0; i < t.length; i += 2) {
			params.put(t[i], t[i + 1]);
		}
		return params;
	}

	private boolean isSameParams(Map<String, String> params1, Map<String, String> params2) {
		if (params1 == null) {
			params1 = Collections.emptyMap();
		}
		if (params2 == null) {
			params2 = Collections.emptyMap();
		}
		if (params1.size() != params2.size()) {
			return false;
		}
		for (Entry<String, String> entry : params1.entrySet()) {
			String value1 = entry.getValue();
			String value2 = params2.get(entry.getKey());
			if (value1 == value2) {
				continue;
			}
			if (!value1.equals(value2)) {
				return false;
			}
		}
		return true;
	}

	private void assertComponents(UrlComponents components, String host, String path, Map<String, String> params) {
		assertComponents(components, host, path, params, null);
	}

	private void assertComponents(UrlComponents components, String host, String path, Map<String, String> params,
			String hash) {
		assertEquals(host, components.getHost());
		assertEquals(path, components.getPath());
		assertTrue(isSameParams(components.getQueryParams(), params));
		if (hash == null) {
			assertNull(components.getHash());
		} else {
			assertEquals(hash, components.getHash());
		}
	}

}
