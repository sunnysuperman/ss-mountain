package com.sunnysuperman.mountain.localevent;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.sunnysuperman.mountain.lang.threadpool.ThreadPoolExecutorProperties;

@Valid
public class LocalEventProperties {

	@Valid
	public static class GroupedThreadPoolExecutorProperties {
		@NotEmpty
		String name;

		@NotEmpty
		ThreadPoolExecutorProperties executor;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ThreadPoolExecutorProperties getExecutor() {
			return executor;
		}

		public void setExecutor(ThreadPoolExecutorProperties executor) {
			this.executor = executor;
		}

	}

	/** 执行器列表 **/
	@Valid
	private List<GroupedThreadPoolExecutorProperties> executors;

	public List<GroupedThreadPoolExecutorProperties> getExecutors() {
		return executors;
	}

	public void setExecutors(List<GroupedThreadPoolExecutorProperties> executors) {
		this.executors = executors;
	}

}
