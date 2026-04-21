package com.sunnysuperman.mountain.localcache.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sunnysuperman.mountain.base.EnvHelper;

@Configuration(proxyBeanMethods = false)
public class RedisLocalCacheAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.localcache.redis")
	public RedisLocalCacheProperties redisLocalCacheProperties() {
		return new RedisLocalCacheProperties();
	}

	@Bean
	public RedisLocalCacheFactory redisLocalCacheFactory(RedisLocalCacheProperties properties, EnvHelper envHelper) {
		return new RedisLocalCacheFactory(properties, envHelper);
	}

}
