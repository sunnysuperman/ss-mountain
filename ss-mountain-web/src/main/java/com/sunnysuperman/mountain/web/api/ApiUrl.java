package com.sunnysuperman.mountain.web.api;

import org.springframework.core.env.Environment;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lang.utils.Url;

public class ApiUrl {
	private Environment env;
	private ApiProperties apiProperties;

	public ApiUrl(Environment env, ApiProperties apiProperties) {
		super();
		this.env = env;
		this.apiProperties = apiProperties;
	}

	public String get(String path) {
		String contextPath = env.getProperty("server.servlet.context-path");
		if (Str.isEmpty(apiProperties.getUrl())) {
			throw new UnexpectedException("Require api.url");
		}
		if (Str.isEmpty(contextPath)) {
			throw new UnexpectedException("No context path set");
		}
		if (Str.isEmpty(apiProperties.getUrl())) {
			throw new UnexpectedException("No api url set");
		}
		return Url.appendPath(apiProperties.getUrl(), contextPath, path);
	}

}
