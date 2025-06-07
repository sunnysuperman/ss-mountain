package com.sunnysuperman.mountain.cache.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.cache.CacheFactory;
import com.sunnysuperman.mountain.task.TaskAutoConfiguration;
import com.sunnysuperman.mountain.task.TaskHelper;

@Configuration(proxyBeanMethods = false)
@Import({ TaskAutoConfiguration.class })
public class RedisCacheAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.cache.redis")
	public RedisCacheProperties redisCacheProperties() {
		return new RedisCacheProperties();
	}

	@Bean
	public CacheFactory cacheFactory(RedisCacheProperties properties, EnvHelper envHelper, TaskHelper taskHelper) {
		return new RedisCacheFactory(properties, envHelper, taskHelper);
	}

}
