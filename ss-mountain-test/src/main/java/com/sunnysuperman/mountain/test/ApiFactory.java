package com.sunnysuperman.mountain.test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.test.proxy.MultipartFileProxy;

public abstract class ApiFactory {

	@SuppressWarnings("unchecked")
	public <T> T createApi(Class<T> clazz, String contextPath) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback((MethodInterceptor) (o, method, params, methodProxy) -> {
			Map<String, Object> paramsMap = new HashMap<>();
			boolean postJSON = false;
			Object requestBody = null;
			Parameter[] methodParams = method.getParameters();
			for (int i = 0; i < methodParams.length; i++) {
				Parameter methodParam = methodParams[i];
				Object methodParamVal = params[i];
				if (methodParam.getAnnotation(RequestBody.class) != null) {
					postJSON = true;
					requestBody = methodParamVal;
					continue;
				}
				if (methodParamVal != null) {
					serializeRequest(methodParam.getName(), methodParamVal, paramsMap);
				}
			}
			String path = getRequestPath(method);
			ApiMock mock = getApiMock(path, method);
			if (postJSON) {
				mock.postJSON(encodePath(contextPath, path, paramsMap), requestBody);
			} else if (method.getAnnotation(PostMapping.class) != null) {
				mock.post(encodePath(contextPath, path, null), paramsMap);
			} else {
				mock.get(encodePath(contextPath, path, paramsMap));
			}
			mock.assertOK();
			return deserializeResponse(mock, method.getGenericReturnType());
		});
		return (T) enhancer.create();
	}

	/** 获取接口请求器 **/
	protected abstract ApiMock getApiMock(String path, Method method);

	/** 方法参数序列化为接口参数 **/
	protected void serializeRequest(String paramName, Object paramValue, Map<String, Object> params) {
		// 特殊参数类型需要转换一下
		if (paramValue instanceof MultipartFileProxy) {
			params.put(paramName, ((MultipartFileProxy) paramValue).getFile());
			return;
		}
		if (paramValue instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) paramValue;
			Enumeration<String> enu = request.getParameterNames();
			while (enu.hasMoreElements()) {
				String key = enu.nextElement();
				params.put(key, request.getParameter(key));
			}
			return;
		}
		params.put(paramName, paramValue);
	}

	/** 获取请求路径 **/
	protected String getRequestPath(Method method) {
		String path1 = getRequestPath(method.getDeclaringClass().getAnnotations());
		String path2 = getRequestPath(method.getAnnotations());
		return Str.nullToEmpty(path1) + Str.nullToEmpty(path2);
	}

	protected String getRequestPath(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> type = annotation.annotationType();
			if (type == PostMapping.class) {
				return ((PostMapping) annotation).value()[0];
			}
			if (type == GetMapping.class) {
				return ((GetMapping) annotation).value()[0];
			}
			if (type == RequestMapping.class) {
				return ((RequestMapping) annotation).value()[0];
			}
		}
		return Str.EMPTY;
	}

	/** 接口返回结果反序列化为方法返回类型 **/
	protected Object deserializeResponse(ApiMock mock, Type respType) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return mapper.readValue(mock.getResponseBody(), mapper.getTypeFactory().constructType(respType));
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	private String encodePath(String contextPath, String path, Map<String, Object> params) {
		if (contextPath == null || contextPath.equals("/")) {
			contextPath = "";
		}
		if (!path.startsWith("/")) {
			path = '/' + path;
		}
		String prefix = contextPath + path;
		String paramsStr = paramsAsString(params);
		if (Str.isEmpty(paramsStr)) {
			return prefix;
		}
		return prefix + (path.indexOf('?') < 0 ? '?' : '&') + paramsStr;
	}

	private String paramsAsString(Map<String, Object> params) {
		if (params == null || params.isEmpty()) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		for (Entry<String, Object> entry : params.entrySet()) {
			if (first) {
				first = false;
			} else {
				buf.append('&');
			}
			buf.append(entry.getKey()).append('=');
			String value = Str.parse(entry.getValue());
			if (Str.isNotEmpty(value)) {
				buf.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
			}
		}
		return buf.toString();
	}

}
