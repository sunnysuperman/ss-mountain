package com.sunnysuperman.mountain.web.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.sunnysuperman.mountain.web.api.ApiProperties;
import com.sunnysuperman.mountain.web.api.ApiUrl;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty("ss-mountain.web.api.url")
public class WebApiAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.web.api")
	public ApiProperties webApiProperties() {
		return new ApiProperties();
	}

	@Bean
	public ApiUrl apiUrl(Environment environment, ApiProperties apiProperties) {
		return new ApiUrl(environment, apiProperties);
	}

}
