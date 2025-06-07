package com.sunnysuperman.mountain.randomid.job;

import com.sunnysuperman.mountain.job.executor.JobExecutor;
import com.sunnysuperman.mountain.randomid.RandomIdGenerator;
import com.sunnysuperman.mountain.randomid.RandomIdGeneratorFactory;

/**
 * 随机ID更新守卫定时任务
 * 
 * 建议配置定时(每小时检查一下): 0 30 * * * ?
 **/
public class RandomIdSentinelJobExecutor implements JobExecutor {
	private RandomIdGeneratorFactory generatorFactory;

	public RandomIdSentinelJobExecutor(RandomIdGeneratorFactory generatorFactory) {
		super();
		this.generatorFactory = generatorFactory;
	}

	@Override
	public String name() {
		return "commons-randomid-sentinel";
	}

	@Override
	public boolean execute() {
		generatorFactory.getGenerators().forEach(RandomIdGenerator::refresh);
		return true;
	}

}
