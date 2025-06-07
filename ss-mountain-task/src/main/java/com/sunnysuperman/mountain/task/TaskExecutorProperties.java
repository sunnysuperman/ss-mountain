package com.sunnysuperman.mountain.task;

import com.sunnysuperman.mountain.lang.threadpool.ThreadPoolExecutorProperties;

public class TaskExecutorProperties {

	/** 是否使用公共线程池(如果是，则executor配置失效) **/
	private boolean useGeneric = true;

	/** 自定义线程池配置 **/
	private ThreadPoolExecutorProperties executor;

	public static TaskExecutorProperties defaults() {
		return new TaskExecutorProperties();
	}

	public void validate() {
		if (useGeneric) {
			executor = null;
		} else {
			if (executor == null) {
				throw new IllegalArgumentException("executor");
			}
			executor.validate();
		}
	}

	public boolean isUseGeneric() {
		return useGeneric;
	}

	public void setUseGeneric(boolean useGeneric) {
		this.useGeneric = useGeneric;
	}

	public ThreadPoolExecutorProperties getExecutor() {
		return executor;
	}

	public void setExecutor(ThreadPoolExecutorProperties executor) {
		this.executor = executor;
	}

}
