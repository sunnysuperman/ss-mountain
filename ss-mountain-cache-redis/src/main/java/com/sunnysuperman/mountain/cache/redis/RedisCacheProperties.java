package com.sunnysuperman.mountain.cache.redis;

import javax.annotation.PostConstruct;

import com.sunnysuperman.mountain.task.TaskExecutorProperties;

public class RedisCacheProperties extends RedisProperties {
	/** 是否使用应用名称作为命名空间 **/
	private boolean useAppNameAsNamespace;

	/** 应用名称，默认取spring应用名 **/
	private String appName;

	/** 是否使用环境名称作为命名空间 **/
	private boolean useProfileAsNamespace;

	/** 环境名称，默认取spring环境名(spring.profiles.active) **/
	private String profile;

	/** 在缓存失败的情况下，需要异步任务处理缓存，保证唯一性 **/
	private TaskExecutorProperties taskExecutor;

	@PostConstruct
	public void init() {
		if (taskExecutor == null) {
			taskExecutor = TaskExecutorProperties.defaults();
		} else {
			taskExecutor.validate();
		}
	}

	public boolean isUseAppNameAsNamespace() {
		return useAppNameAsNamespace;
	}

	public void setUseAppNameAsNamespace(boolean useAppNameAsNamespace) {
		this.useAppNameAsNamespace = useAppNameAsNamespace;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean isUseProfileAsNamespace() {
		return useProfileAsNamespace;
	}

	public void setUseProfileAsNamespace(boolean useProfileAsNamespace) {
		this.useProfileAsNamespace = useProfileAsNamespace;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public TaskExecutorProperties getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(TaskExecutorProperties taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

}
