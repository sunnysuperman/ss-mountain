package com.sunnysuperman.mountain.job.xxl;

import com.sunnysuperman.mountain.job.JobRegistrar;
import com.sunnysuperman.mountain.job.executor.JobExecutor;
import com.xxl.job.core.executor.XxlJobExecutor;

public class XxlJobRegistrar implements JobRegistrar {

	@Override
	public void register(JobExecutor e) {
		XxlJobExecutor.registJobHandler(e.name(), new XxlJobHandler(e));
	}

}
