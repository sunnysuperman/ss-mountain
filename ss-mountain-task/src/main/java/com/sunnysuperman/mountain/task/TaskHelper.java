package com.sunnysuperman.mountain.task;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.threadpool.ScheduledThreadPoolExecutor;
import com.sunnysuperman.mountain.lang.threadpool.ThreadPoolExecutors;
import com.sunnysuperman.mountain.lang.utils.Jsons;

public class TaskHelper {
	private static final Logger LOG = LoggerFactory.getLogger(TaskHelper.class);

	private GenericTaskProperties genericTaskProperties;
	private ScheduledTaskProperties scheduledTaskProperties;
	private ParallelTaskProperties parallelTaskProperties;

	private Executor genericExecutor;
	private ScheduledThreadPoolExecutor scheduledExecutor;
	private Executor parallelExecutor;

	public TaskHelper(GenericTaskProperties genericTaskProperties, ScheduledTaskProperties scheduledTaskProperties,
			ParallelTaskProperties parallelTaskProperties) {
		super();
		this.genericTaskProperties = genericTaskProperties;
		this.scheduledTaskProperties = scheduledTaskProperties;
		this.parallelTaskProperties = parallelTaskProperties;
	}

	@PostConstruct
	public void init() {
		// 普通线程池
		if (genericTaskProperties != null && genericTaskProperties.isEnabled()) {
			genericExecutor = ThreadPoolExecutors.newExecutor(genericTaskProperties);
			if (LOG.isInfoEnabled()) {
				LOG.info(">>>>>>[task] generic config {}", Jsons.write(genericTaskProperties));
			}
		}
		// 定时线程池
		if (scheduledTaskProperties != null && scheduledTaskProperties.isEnabled()) {
			scheduledExecutor = ThreadPoolExecutors.newScheduledExecutor(scheduledTaskProperties);
			if (LOG.isInfoEnabled()) {
				LOG.info(">>>>>>[task] scheduled config {}", Jsons.write(scheduledTaskProperties));
			}
		}
		// 并行工作线程池
		if (parallelTaskProperties != null && parallelTaskProperties.isEnabled()) {
			// 并行工作线程，不排队，没有线程资源的情况下，任务直接在调用线程执行
			parallelExecutor = ThreadPoolExecutors.newExecutor(parallelTaskProperties);
			if (LOG.isInfoEnabled()) {
				LOG.info(">>>>>>[task] parallel config {}", Jsons.write(parallelTaskProperties));
			}
		}
	}

	public boolean isGenericTasksEnabled() {
		return genericExecutor != null;
	}

	public boolean isScheduledTasksEnabled() {
		return scheduledExecutor != null;
	}

	public boolean isParallelTasksEnabled() {
		return parallelExecutor != null;
	}

	public void addTask(Runnable task) {
		if (genericExecutor == null) {
			throw new UnsupportedOperationException("未设置任务线程池");
		}
		try {
			genericExecutor.execute(task);
		} catch (Exception ex) {
			LOG.error("Add task failed", ex);
		}
	}

	public boolean scheduleTask(Runnable task, long delay) {
		if (scheduledExecutor == null) {
			throw new UnsupportedOperationException("未设置延迟任务线程池");
		}
		return scheduledExecutor.schedule(task, delay);
	}

	@SuppressWarnings("rawtypes")
	public void runInParallel(long timeoutInMillis, Runnable... tasks)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (parallelExecutor == null) {
			throw new UnsupportedOperationException("未设置并行任务线程池");
		}
		CompletableFuture[] futures = Arrays.stream(tasks)
				.map(task -> CompletableFuture.runAsync(task, parallelExecutor)).collect(Collectors.toList())
				.toArray(CompletableFuture[]::new);
		CompletableFuture future = CompletableFuture.allOf(futures);
		if (timeoutInMillis > 0) {
			future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
		} else {
			future.join();
		}
	}

	public void runInParallel(Runnable... tasks) throws InterruptedException, ExecutionException, TimeoutException {
		runInParallel(0, tasks);
	}
}
