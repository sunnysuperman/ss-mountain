package com.sunnysuperman.mountain.lang.threadpool;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.sunnysuperman.mountain.lang.exception.Exceptions;

public class ThreadPoolExecutors {

	private ThreadPoolExecutors() {
	}

	public static Executor newExecutor(ThreadPoolExecutorProperties props, RejectedExecutionHandler rejectHandler) {
		props.validate();
		long keepAliveTime = props.getMaxThreadsNum() > props.getThreadsNum() ? props.getKeepAliveTime() : 0;
		if (rejectHandler == null) {
			String rejectHandlerClass = props.getRejectHandler();
			if (rejectHandlerClass.indexOf('.') < 0) {
				rejectHandlerClass = "java.util.concurrent.ThreadPoolExecutor$" + rejectHandlerClass;
			}
			try {
				rejectHandler = (RejectedExecutionHandler) Class.forName(rejectHandlerClass).getConstructor()
						.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				throw Exceptions.wrapRuntimeException("Not a valid reject handler: " + rejectHandlerClass, e);
			}
		}
		return new ThreadPoolExecutor(props.getThreadsNum(), props.getMaxThreadsNum(), keepAliveTime, TimeUnit.MINUTES,
				new LinkedBlockingQueue<>(props.getQueueCapacity()),
				new NamedThreadFactory(props.getThreadNamePrefix()), rejectHandler);
	}

	public static Executor newExecutor(ThreadPoolExecutorProperties props) {
		return newExecutor(props, null);
	}

	public static ScheduledThreadPoolExecutor newScheduledExecutor(ThreadPoolExecutorProperties props) {
		return new ScheduledThreadPoolExecutor(props);
	}

	@SuppressWarnings("squid:S3014")
	private static class NamedThreadFactory implements ThreadFactory {
		private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String threadNamePrefix;

		NamedThreadFactory(String threadNamePrefix) {
			SecurityManager s = System.getSecurityManager();
			this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.threadNamePrefix = threadNamePrefix != null ? threadNamePrefix
					: "namedpool-" + POOL_NUMBER.getAndIncrement() + "-";
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r, threadNamePrefix + threadNumber.getAndIncrement(), 0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}

}
