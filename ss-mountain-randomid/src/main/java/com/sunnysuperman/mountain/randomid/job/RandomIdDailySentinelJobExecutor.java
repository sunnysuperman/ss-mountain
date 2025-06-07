package com.sunnysuperman.mountain.randomid.job;

import com.sunnysuperman.mountain.job.executor.JobExecutor;
import com.sunnysuperman.mountain.randomid.RandomIdGenerator;
import com.sunnysuperman.mountain.randomid.RandomIdGeneratorFactory;

/**
 * 随机ID每日更新定时任务（提前生成第二天的）
 * 
 * 建议配置定时(每天晚上11点，每隔10分钟检查一下): 0 0,10,20,30,40,50 23 * * ?
 **/
public class RandomIdDailySentinelJobExecutor implements JobExecutor {
	private RandomIdGeneratorFactory randomIdGeneratorFactory;

	public RandomIdDailySentinelJobExecutor(RandomIdGeneratorFactory randomIdGeneratorFactory) {
		super();
		this.randomIdGeneratorFactory = randomIdGeneratorFactory;
	}

	@Override
	public String name() {
		return "commons-randomid-daily-sentinel";
	}

	@Override
	public boolean execute() {
		randomIdGeneratorFactory.getGenerators().forEach(RandomIdGenerator::refreshNextDay);
		return true;
	}

}
