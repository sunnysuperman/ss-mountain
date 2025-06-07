package com.sunnysuperman.mountain.lock.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.lock.LockHelper;

@Configuration(proxyBeanMethods = false)
public class RedisLockAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.lock.redis")
	public RedisLockProperties lockProperties() {
		return new RedisLockProperties();
	}

	@Bean
	public LockHelper lockHelper(RedisLockProperties properties, EnvHelper envHelper) {
		return new RedisLockHelper(properties, envHelper);
	}

}
