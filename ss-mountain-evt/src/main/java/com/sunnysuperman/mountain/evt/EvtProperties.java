package com.sunnysuperman.mountain.evt;

import javax.annotation.PostConstruct;

import com.sunnysuperman.mountain.task.TaskExecutorProperties;

public class EvtProperties {

	/** 事件模型扫描包 **/
	private String[] scanPackages;

	/** 禁用MQ **/
	private boolean mqDisabled;

	/** 不启用MQ的情况下或MQ失效的情况下，消费事件改成本地处理 **/
	private TaskExecutorProperties taskExecutor;

	@PostConstruct
	public void init() {
		if (taskExecutor == null) {
			taskExecutor = TaskExecutorProperties.defaults();
		} else {
			taskExecutor.validate();
		}
	}

	public String[] getScanPackages() {
		return scanPackages;
	}

	public void setScanPackages(String[] scanPackages) {
		this.scanPackages = scanPackages;
	}

	public boolean isMqDisabled() {
		return mqDisabled;
	}

	public void setMqDisabled(boolean mqDisabled) {
		this.mqDisabled = mqDisabled;
	}

	public TaskExecutorProperties getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(TaskExecutorProperties taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

}
