package com.sunnysuperman.mountain.task;

import java.util.concurrent.Executor;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.threadpool.ThreadPoolExecutorProperties;
import com.sunnysuperman.mountain.lang.threadpool.ThreadPoolExecutors;

public class TaskExecutor {
	TaskHelper taskHelper;
	ThreadPoolExecutorProperties props;
	Executor executor;

	private TaskExecutor() {
	}

	public static TaskExecutor of(TaskHelper taskHelper, TaskExecutorProperties props) {
		TaskExecutor wrap = new TaskExecutor();
		wrap.taskHelper = taskHelper;
		if (props.isUseGeneric()) {
			if (!taskHelper.isGenericTasksEnabled()) {
				throw new UnexpectedException("Ensure task.generic.enabled!!!");
			}
		} else {
			wrap.executor = ThreadPoolExecutors.newExecutor(props.getExecutor());
		}
		return wrap;
	}

	public void execute(Runnable task) {
		if (executor != null) {
			executor.execute(task);
		} else {
			taskHelper.addTask(task);
		}
	}

}
