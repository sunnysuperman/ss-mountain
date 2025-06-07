package com.sunnysuperman.mountain.lang.task;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务引擎类
 * 
 * 
 *
 */
public class TaskEngine {
	/**
	 * 日志记录
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(TaskEngine.class);
	/**
	 * 任务列表
	 */
	protected final LinkedList<Runnable> taskList = new LinkedList<>();
	/**
	 * 添加task的同步锁
	 */
	protected final Object addLock = new byte[0];
	/**
	 * 任务定时器
	 */
	protected Timer taskTimer = null;
	/**
	 * 线程
	 */
	protected Thread[] workers = null;
	/**
	 * 是否停止
	 */
	protected boolean stopped = false;
	/**
	 * 是否启用日志
	 */
	protected boolean logEnabled = false;

	/**
	 * 工作线程，用于执行taskList中的task
	 * 
	 * 
	 *
	 */
	private class TaskEngineWorker extends Thread {

		@Override
		public void run() {
			while (!stopped) {
				Runnable task = nextTask();
				if (task == null) {
					break;
				}
				try {
					task.run();
				} catch (Exception e) {
					LOGGER.error(null, e);
				}
			}
		}
	}

	/**
	 * 调度任务类，此任务的作用是负责将task加入到{@linkplain TaskEngine}的taskList中
	 * 
	 * 
	 *
	 */
	private class ScheduledTask extends TimerTask {

		private Runnable task;

		public ScheduledTask(Runnable task) {
			this.task = task;
		}

		@Override
		public void run() {
			addTask(task);
		}
	}

	/**
	 * 新建一个工作线程
	 * 
	 * @return 工作线程
	 */
	protected Thread newTaskEngineWorker() {
		return new TaskEngineWorker();
	}

	/**
	 * 获取下一个任务<br>
	 * 此方法添加了addLock锁，与{@linkplain #addTask(Runnable)}和{@linkplain #exit()}线程同步 <br>
	 * 若engine停止，则返回null；若taskList为空，则线程等待；否则，删除taskList最后一个task且返回该task
	 * 
	 * @return 下一个任务
	 */
	protected Runnable nextTask() {
		synchronized (addLock) {
			if (stopped) {
				return null;
			}
			while (taskList.isEmpty()) {
				if (stopped) {
					return null;
				}
				try {
					addLock.wait();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
			return taskList.removeLast();
		}
	}

	/**
	 * 构造函数<br>
	 * 启动workerNum个线程，且设置为后台线程
	 * 
	 * @param workerNum 启动的线程数
	 */
	public TaskEngine(String groupName, int workerNum) {
		taskTimer = new Timer(true);
		workers = new Thread[workerNum];
		for (int i = 0; i < workers.length; i++) {
			/**
			 * 此处使用模板方法，子类可复写此模板，生成新特定功能的工作线程
			 */
			workers[i] = newTaskEngineWorker();
			workers[i].setName(groupName + "-" + (i + 1));
			workers[i].setDaemon(true);
			workers[i].start();
		}
	}

	/**
	 * 是否开启日志记录
	 * 
	 * @return 若开启了，则返回true；否则，返回false
	 */
	public boolean isLogEnabled() {
		return logEnabled;
	}

	/**
	 * 设置是否开启日志记录
	 * 
	 * @param logEnabled 是否记录日志
	 */
	public void setLogEnabled(boolean logEnabled) {
		this.logEnabled = logEnabled;
	}

	/**
	 * 添加新任务<br>
	 * 此方法添加了addLock锁，与{@linkplain #nextTask()}和{@linkplain #exit()}线程同步
	 * 
	 * @param task 带添加的任务
	 */
	public void addTask(Runnable task) {
		synchronized (addLock) {
			taskList.addFirst(task);
			addLock.notifyAll();
		}
	}

	/**
	 * 获取task个数
	 * 
	 * @return task个数
	 */
	public int getTasksNum() {
		return taskList.size();
	}

	/**
	 * 退出engine<br>
	 * 此方法添加了addLock锁，与{@linkplain #nextTask()}和{@linkplain #addTask(Runnable)}
	 * 线程同步<br>
	 * 调用任务列表中每一个线程的{@linkplain Thread#join()}方法，等待消亡
	 */
	public void exit() {
		synchronized (addLock) {
			stopped = true;
			addLock.notifyAll();
		}
		for (Thread worker : workers) {
			try {
				worker.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * 调度一个任务的执行，重复执行
	 * 
	 * @param task   待安排的任务
	 * @param delay  延迟多少毫秒后，任务开始执行
	 * @param period 任务执行的周期
	 * @return 调度的任务
	 */
	public TimerTask scheduleTask(Runnable task, long delay, long period) {
		TimerTask timerTask = new ScheduledTask(task);
		taskTimer.schedule(timerTask, delay, period);
		return timerTask;
	}

	/**
	 * 调度一个任务的执行，只执行一次
	 * 
	 * @param task  待安排的任务
	 * @param delay 延迟多少毫秒后，任务开始执行
	 * @return 调度的任务
	 */
	public TimerTask scheduleTask(Runnable task, long delay) {
		TimerTask timerTask = new ScheduledTask(task);
		taskTimer.schedule(timerTask, delay);
		return timerTask;
	}
}
