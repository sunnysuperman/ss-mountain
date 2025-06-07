package com.sunnysuperman.mountain.randomid;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sunnysuperman.mountain.cache.CacheFactory;
import com.sunnysuperman.mountain.cache.redis.RedisCacheAutoConfiguration;
import com.sunnysuperman.mountain.cache.redis.RedisCacheFactory;
import com.sunnysuperman.mountain.lock.LockHelper;
import com.sunnysuperman.mountain.randomid.job.RandomIdDailySentinelJobExecutor;
import com.sunnysuperman.mountain.randomid.job.RandomIdSentinelJobExecutor;

@Configuration(proxyBeanMethods = false)
@Import({ RedisCacheAutoConfiguration.class })
public class RandomIdAutoConfiguration {

	@Bean
	public RandomIdGeneratorFactory randomIdGeneratorFactory(CacheFactory cacheFactory, LockHelper lockHelper) {
		return new RandomIdGeneratorFactory((RedisCacheFactory) cacheFactory, lockHelper);
	}

	@Bean
	public RandomIdDailySentinelJobExecutor randomIdDailySentinelJobExecutor(
			RandomIdGeneratorFactory randomIdGeneratorFactory) {
		return new RandomIdDailySentinelJobExecutor(randomIdGeneratorFactory);
	}

	@Bean
	public RandomIdSentinelJobExecutor randomIdSentinelJobExecutor(RandomIdGeneratorFactory randomIdGeneratorFactory) {
		return new RandomIdSentinelJobExecutor(randomIdGeneratorFactory);
	}

}
