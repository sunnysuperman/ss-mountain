package com.sunnysuperman.mountain.cache.redis;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.cache.Cache;
import com.sunnysuperman.mountain.cache.CacheFactory;
import com.sunnysuperman.mountain.cache.CacheImpl;
import com.sunnysuperman.mountain.cache.CacheOptions;
import com.sunnysuperman.mountain.cache.CachePolicy;
import com.sunnysuperman.mountain.cache.CacheSaveFilter;
import com.sunnysuperman.mountain.cache.converter.Converter;
import com.sunnysuperman.mountain.cache.provider.RepositoryProvider;
import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.task.ScheduledTaskExecutor;
import com.sunnysuperman.mountain.task.TaskHelper;

import redis.clients.jedis.JedisPool;

/** 缓存工厂 **/
public class RedisCacheFactory implements CacheFactory {
	private static final Logger LOG = LoggerFactory.getLogger(RedisCacheFactory.class);

	@Resource
	private RedisCacheProperties redisCacheProperties;
	@Resource
	private EnvHelper envHelper;
	@Resource
	private TaskHelper taskHelper;

	private RedisCacheExecutor executor;
	private Set<String> registeredKeys = new HashSet<>();
	private ScheduledTaskExecutor scheduledTaskExecutor;

	public RedisCacheFactory(RedisCacheProperties redisCacheProperties, EnvHelper envHelper, TaskHelper taskHelper) {
		super();
		this.redisCacheProperties = redisCacheProperties;
		this.envHelper = envHelper;
		this.taskHelper = taskHelper;
		// 异步执行器配置
		scheduledTaskExecutor = ScheduledTaskExecutor.of(taskHelper, redisCacheProperties.getTaskExecutor());
		// 创建redis缓存执行器
		executor = new RedisCacheExecutor(RedisUtils.createPool(redisCacheProperties));
		// 测试连接
		if (LOG.isInfoEnabled()) {
			LOG.info(">>>>>>[RedisCache] start to init: '{}'", Jsons.write(redisCacheProperties));
		}
		executor.find("0", new CachePolicy().setPrefix("0"));
		// 启动成功
		if (LOG.isInfoEnabled()) {
			LOG.info(">>>>>>[RedisCache] initialized, key-prefix: '{}'", getPrefix());
		}
	}

	@Override
	public <T, K> Cache<T, K> create(CacheOptions options, RepositoryProvider<T, K> repository, Converter<T> converter,
			CacheSaveFilter<T, K> saveFilter) {
		if (Str.isEmpty(options.getKey())) {
			throw new IllegalArgumentException("Bad cache key");
		}
		if (registeredKeys.contains(options.getKey())) {
			throw new IllegalArgumentException("Duplicate cache key:" + options.getKey());
		}
		registeredKeys.add(options.getKey());
		if (options.getVersion() <= 0) {
			throw new IllegalArgumentException("Bad cache version");
		}
		if (options.getExpireIn() <= 0) {
			throw new IllegalArgumentException("Bad cache expireIn");
		}
		CachePolicy policy = new CachePolicy();
		policy.setPrefix(getPrefix() + options.getKey() + ":" + options.getVersion() + ":");
		policy.setExpireIn(options.getExpireIn());
		return new CacheImpl<>(executor, policy, repository, converter, saveFilter, scheduledTaskExecutor);
	}

	public JedisPool getPool() {
		return executor.getPool();
	}

	public String getPrefix() {
		StringBuilder b = new StringBuilder();
		if (redisCacheProperties.isUseProfileAsNamespace()) {
			b.append(Str.or(redisCacheProperties.getProfile(), envHelper.getProfile())).append(':');
		}
		if (redisCacheProperties.isUseAppNameAsNamespace()) {
			b.append(Str.or(redisCacheProperties.getAppName(), envHelper.getApplicationName())).append(':');
		}
		return b.toString();
	}

}
