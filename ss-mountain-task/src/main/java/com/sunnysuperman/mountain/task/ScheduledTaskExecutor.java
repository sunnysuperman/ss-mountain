package com.sunnysuperman.mountain.task;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.threadpool.ScheduledThreadPoolExecutor;
import com.sunnysuperman.mountain.lang.threadpool.ThreadPoolExecutorProperties;
import com.sunnysuperman.mountain.lang.threadpool.ThreadPoolExecutors;

public class ScheduledTaskExecutor {
	TaskHelper taskHelper;
	ThreadPoolExecutorProperties props;
	ScheduledThreadPoolExecutor executor;

	private ScheduledTaskExecutor() {
	}

	public static ScheduledTaskExecutor of(TaskHelper taskHelper, TaskExecutorProperties props) {
		ScheduledTaskExecutor wrap = new ScheduledTaskExecutor();
		wrap.taskHelper = taskHelper;
		if (props.isUseGeneric()) {
			if (!taskHelper.isScheduledTasksEnabled()) {
				throw new UnexpectedException("Ensure task.scheduled.enabled!!!");
			}
		} else {
			wrap.executor = ThreadPoolExecutors.newScheduledExecutor(props.getExecutor());
		}
		return wrap;
	}

	public boolean schedule(Runnable task, long delay) {
		if (executor != null) {
			return executor.schedule(task, delay);
		} else {
			return taskHelper.scheduleTask(task, delay);
		}
	}

}
