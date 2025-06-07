package com.sunnysuperman.mountain.lang.threadpool;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.task.TaskEngine;

public class ScheduledThreadPoolExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(ScheduledThreadPoolExecutor.class);
	private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
	private ThreadPoolExecutorProperties props;
	private TaskEngine engine;

	ScheduledThreadPoolExecutor(ThreadPoolExecutorProperties props) {
		super();
		props.validate();
		this.props = props;
		String threadNamePrefix = props.getThreadNamePrefix() != null ? props.getThreadNamePrefix()
				: "scheduledpool-" + POOL_NUMBER.getAndIncrement() + "-";
		engine = new TaskEngine(threadNamePrefix, props.getMaxThreadsNum());
	}

	public boolean schedule(Runnable runnable, long delay) {
		if (delay <= 0) {
			engine.addTask(runnable);
			return true;
		}
		try {
			if (engine.getTasksNum() >= props.getQueueCapacity()) {
				LOG.warn("[ScheduledExecutor] exceed queue capacity!!!!!!");
				return false;
			}
			engine.scheduleTask(runnable, delay);
			return true;
		} catch (Exception ex) {
			LOG.error("[ScheduledExecutor] schedule failed", ex);
			return false;
		}
	}

}
