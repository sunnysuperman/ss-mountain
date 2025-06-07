package com.sunnysuperman.mountain.localevent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.exception.Exceptions;
import com.sunnysuperman.mountain.lang.threadpool.ThreadPoolExecutors;
import com.sunnysuperman.mountain.lang.utils.Colls;
import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.localevent.LocalEventProperties.GroupedThreadPoolExecutorProperties;
import com.sunnysuperman.mountain.task.TaskHelper;

public class LocalEventPublisher {
	private static final Logger LOG = LoggerFactory.getLogger(LocalEventPublisher.class);

	private LocalEventProperties localEventProperties;
	private LocalEventSubscriberManager localEventSubscriberManager;
	private TaskHelper taskHelper;
	/** 任务执行器 **/
	private Map<String, Executor> executorMap;
	private Executor defaultExecutor;

	public LocalEventPublisher(LocalEventProperties localEventProperties,
			LocalEventSubscriberManager localEventSubscriberManager, TaskHelper taskHelper) {
		super();
		this.localEventProperties = localEventProperties;
		this.localEventSubscriberManager = localEventSubscriberManager;
		this.taskHelper = taskHelper;
	}

	@PostConstruct
	public void init() {
		executorMap = Colls.isEmpty(localEventProperties.getExecutors()) ? Collections.emptyMap()
				: localEventProperties.getExecutors().stream()
						.collect(Collectors.toMap(GroupedThreadPoolExecutorProperties::getName,
								i -> ThreadPoolExecutors.newExecutor(i.getExecutor())));
		defaultExecutor = executorMap.size() == 1 ? executorMap.values().iterator().next() : null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void publish(Object event, String groupName) {
		Objects.requireNonNull(event);
		try {
			execute(groupName, () -> {
				try {
					List<Consumer> subscribers = localEventSubscriberManager.findSubscribers(event.getClass());
					if (subscribers == null) {
						LOG.error("No consumers for event: {}", event.getClass());
						return;
					}
					subscribers.forEach(subscriber -> subscriber.accept(event));
				} catch (Exception ex) {
					LOG.error("Failed to consume event: " + event.getClass(), ex);
				}
			});
		} catch (Exception ex) {
			LOG.error("Failed to publish event: " + Jsons.write(event), ex);
		}
	}

	public void publish(Object event) {
		publish(event, null);
	}

	private void execute(String groupName, Runnable task) {
		if (groupName != null) {
			// 配置执行器
			Executor executor = executorMap.get(groupName);
			if (executor == null) {
				throw Exceptions.wrapRuntimeException("No executor of group '" + groupName + "'");
			}
			executor.execute(task);
		} else {
			if (defaultExecutor != null) {
				// 配置默认执行器
				defaultExecutor.execute(task);
			} else {
				// 全局执行器
				taskHelper.addTask(task);
			}
		}

	}

}
