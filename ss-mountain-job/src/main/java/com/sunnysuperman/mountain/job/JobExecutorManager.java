package com.sunnysuperman.mountain.job;

import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.sunnysuperman.mountain.job.executor.JobExecutor;
import com.sunnysuperman.mountain.lang.exception.Exceptions;

public class JobExecutorManager {
	private static final Map<String, JobExecutor> executorMap = new ConcurrentHashMap<>();
	private static boolean used = false;

	private JobExecutorManager() {
	}

	/** 添加任务到列表 **/
	public static synchronized void add(JobExecutor executor) {
		ensureNotUsed();
		String name = executor.name();
		// 检查任务名称
		JobUtils.checkJobName(name);
		// 添加到任务列表
		if (executorMap.putIfAbsent(name, executor) != null) {
			throw Exceptions.wrapRuntimeException("Duplicate job name: " + name);
		}
	}

	/** 统计任务数量 **/
	public static synchronized int count() {
		return executorMap.size();
	}

	/** 调用具体任务管理器注册 **/
	public static synchronized void use(JobRegistrar jobRegistrar) {
		ensureNotUsed();
		// 调用具体任务管理器注册
		executorMap.values().forEach(jobRegistrar::register);
		if (JobLogger.isInfoEnabled()) {
			JobLogger.info(">>>>>>[job] registration completed, roster: {}", new TreeSet<>(executorMap.keySet()));
		}
		// 清空节约内存
		executorMap.clear();
		used = true;
	}

	private static void ensureNotUsed() {
		if (used) {
			throw Exceptions.wrapRuntimeException("job registration already completed");
		}
	}

}
