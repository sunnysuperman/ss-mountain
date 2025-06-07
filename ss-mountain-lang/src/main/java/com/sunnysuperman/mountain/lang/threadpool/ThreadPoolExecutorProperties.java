package com.sunnysuperman.mountain.lang.threadpool;

public class ThreadPoolExecutorProperties {

	/** 最大队列长度 **/
	private int queueCapacity = 1000;

	/** 基本线程数 **/
	private int threadsNum = Math.max(Runtime.getRuntime().availableProcessors() * 2, 8);

	/** 最多线程数 **/
	private int maxThreadsNum = 0;

	/** 超过基本线程数的线程闲置时间(分钟) **/
	private long keepAliveTime = 30;

	/** 线程名称前缀 **/
	private String threadNamePrefix;

	/**
	 * 列队已满不能处理策略
	 * 
	 * CallerRunsPolicy/AbortPolicy/DiscardPolicy/DiscardOldestPolicy
	 **/
	private String rejectHandler = "CallerRunsPolicy";

	public void validate() {
		if (queueCapacity <= 0) {
			throw new IllegalArgumentException("queueCapacity");
		}
		if (threadsNum <= 0) {
			throw new IllegalArgumentException("threadsNum");
		}
		if (maxThreadsNum <= 0 || maxThreadsNum < threadsNum) {
			maxThreadsNum = threadsNum;
		}
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	public int getThreadsNum() {
		return threadsNum;
	}

	public void setThreadsNum(int threadsNum) {
		this.threadsNum = threadsNum;
	}

	public int getMaxThreadsNum() {
		return maxThreadsNum;
	}

	public void setMaxThreadsNum(int maxThreadsNum) {
		this.maxThreadsNum = maxThreadsNum;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public String getThreadNamePrefix() {
		return threadNamePrefix;
	}

	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}

	public String getRejectHandler() {
		return rejectHandler;
	}

	public void setRejectHandler(String rejectHandler) {
		this.rejectHandler = rejectHandler;
	}

}
