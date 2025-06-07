package com.sunnysuperman.mountain.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.sunnysuperman.mountain.lang.utils.Colls;
import com.sunnysuperman.mountain.lang.utils.Str;

public class WebUtils {

	private WebUtils() {
	}

	public static final String KEY_AUTHORIZATION = "Authorization";

	public static String getRemoteAddress(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null) {
			List<String> proxies = Str.split(ip, ",");
			String remoteIp = Colls.isNotEmpty(proxies) ? proxies.get(0).trim() : null;
			if (isValidIp(remoteIp)) {
				return remoteIp;
			}
		}
		ip = request.getRemoteAddr();
		if (isValidIp(ip)) {
			return ip;
		}
		return null;
	}

	private static boolean isValidIp(String ip) {
		return ip != null && !ip.isEmpty() && !ip.equalsIgnoreCase("unknown");
	}

	public static Map<String, String> getRequestParams(HttpServletRequest request) {
		Iterator<String> iter = request.getParameterNames().asIterator();
		Map<String, String> kv = new HashMap<>();
		while (iter.hasNext()) {
			String name = iter.next();
			kv.put(name, request.getParameter(name));
		}
		return kv;
	}

	public static String getRequestHeader(HttpServletRequest request, String key) {
		String value = request.getHeader(key);
		if (value != null) {
			return value;
		}
		value = request.getParameter(key);
		if (value != null) {
			return value;
		}
		return null;
	}

	public static String getRequestAuthorization(HttpServletRequest request) {
		return getRequestHeader(request, KEY_AUTHORIZATION);
	}
}
