package com.sunnysuperman.mountain.randomid;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sunnysuperman.mountain.cache.redis.RedisCacheFactory;
import com.sunnysuperman.mountain.lock.LockHelper;

public class RandomIdGeneratorFactory {
	private RedisCacheFactory cacheFactory;
	private LockHelper lockHelper;
	private List<RandomIdGenerator> generators = Collections.emptyList();

	public RandomIdGeneratorFactory(RedisCacheFactory cacheFactory, LockHelper lockHelper) {
		super();
		this.cacheFactory = cacheFactory;
		this.lockHelper = lockHelper;
	}

	public RandomIdGenerator create(RandomIdGeneratorConfig config) {
		RandomIdGenerator generator;
		synchronized (this) {
			generator = new RandomIdGenerator(config, lockHelper, cacheFactory);
			if (generators.isEmpty()) {
				generators = new CopyOnWriteArrayList<>();
			}
			generators.add(generator);
		}
		if (config.isRefreshOnCreated()) {
			generator.refresh();
		}
		return generator;
	}

	public List<RandomIdGenerator> getGenerators() {
		return generators;
	}
}
