package com.sunnysuperman.mountain.job.executor;

public interface JobExecutor {

	/**
	 * 指定任务名称
	 * 
	 * @return 任务名称（建议命名: 模块名称-子模块名称-具体任务，如goods-sku-event）
	 * @throws Exception
	 */
	String name();

	/**
	 * 执行任务
	 * 
	 * @return 成功true，失败false
	 * @throws Exception
	 */
	boolean execute();

}
