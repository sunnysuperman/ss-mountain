package com.sunnysuperman.mountain.lang.task;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompleteAwareTaskEngine {
	private static final Logger LOG = LoggerFactory.getLogger(CompleteAwareTaskEngine.class);

	private ThreadPoolExecutor executor;
	private AtomicInteger tasksNum = new AtomicInteger(0);
	private Lock completedLock = new ReentrantLock();
	private Condition completedSignal = completedLock.newCondition();

	public CompleteAwareTaskEngine(int threadsNum) {
		super();
		executor = new ThreadPoolExecutor(threadsNum, threadsNum, 1, TimeUnit.DAYS, new LinkedBlockingQueue<>(),
				Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
	}

	private class Task implements Runnable {
		Runnable runnable;

		public Task(Runnable runnable) {
			super();
			this.runnable = runnable;
		}

		@Override
		public void run() {
			try {
				runnable.run();
			} catch (Exception e) {
				LOG.error(null, e);
			}
			if (tasksNum.decrementAndGet() == 0) {
				completedLock.lock();
				try {
					completedSignal.signalAll();
				} finally {
					completedLock.unlock();
				}
			}
		}

	}

	public void addTask(Runnable runnable) {
		tasksNum.incrementAndGet();
		executor.submit(new Task(runnable));
	}

	public int getIncompleteTasksNum() {
		return tasksNum.get();
	}

	public void waitUntilDone() {
		if (tasksNum.get() <= 0) {
			return;
		}
		completedLock.lock();
		try {
			while (true) {
				try {
					if (tasksNum.get() <= 0) {
						break;
					}
					completedSignal.await();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} finally {
			completedLock.unlock();
		}
	}

	public void waitUntilDoneAndShutdown() {
		waitUntilDone();
		executor.shutdown();
	}

	public boolean isShutdown() {
		return executor.isShutdown();
	}
}
