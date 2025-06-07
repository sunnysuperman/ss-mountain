package com.sunnysuperman.mountain.web.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sunnysuperman.mountain.web.crossdomain.CrossDomainProperties;

@Configuration(proxyBeanMethods = false)
public class WebCrossDomainAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.web.cross-domain")
	public CrossDomainProperties crossDomainProperties() {
		return new CrossDomainProperties();
	}

}
