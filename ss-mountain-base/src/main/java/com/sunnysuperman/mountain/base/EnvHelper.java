package com.sunnysuperman.mountain.base;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvHelper {
	@Value("${spring.application.name}")
	private String applicationName;

	@Resource
	private Environment env;

	@Resource
	private BaseProperties baseProperties;

	public void setEnv(Environment env) {
		this.env = env;
	}

	public Environment getEnv() {
		return env;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getProfile() {
		return env.getActiveProfiles()[0];
	}

	public boolean isLocal() {
		return baseProperties.isLocal();
	}

	public boolean isStrictMode() {
		return baseProperties.isStrictMode();
	}

}
