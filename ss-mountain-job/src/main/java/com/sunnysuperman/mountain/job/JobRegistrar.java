package com.sunnysuperman.mountain.job;

import com.sunnysuperman.mountain.job.executor.JobExecutor;

public interface JobRegistrar {

	void register(JobExecutor e);

}
