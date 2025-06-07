package com.sunnysuperman.mountain.job.xxl;

import com.sunnysuperman.mountain.job.JobException;
import com.sunnysuperman.mountain.job.JobLogger;
import com.sunnysuperman.mountain.job.executor.JobExecutor;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobHandler;

class XxlJobHandler extends IJobHandler {
	private JobExecutor executor;

	public XxlJobHandler(JobExecutor executor) {
		super();
		this.executor = executor;
	}

	/** 普通日志 **/
	protected void info(String format, Object... args) {
		if (!XxlJobHelper.log(format, args)) {
			JobLogger.info(format, args);
		}
	}

	/** 错误日志 **/
	protected void error(Throwable e) {
		if (!XxlJobHelper.log(e)) {
			JobLogger.error(e);
		}
	}

	@Override
	public final void execute() throws Exception {
		boolean success = executor.execute();
		if (!success) {
			throw new JobException(String.format("任务:'%s' 执行失败", executor.name()));
		}
	}

}
