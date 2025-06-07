package com.sunnysuperman.mountain.localevent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class LocalEventSubscriberManager {
	private final Map<Class<?>, CopyOnWriteArrayList<Consumer>> consumerMap = new ConcurrentHashMap<>();

	public synchronized void register(Class<?> eventClass, Consumer<?> consumer) {
		CopyOnWriteArrayList<Consumer> consumers = consumerMap.computeIfAbsent(eventClass,
				i -> new CopyOnWriteArrayList<Consumer>());
		consumers.add(consumer);
	}

	public synchronized List<Consumer> findSubscribers(Class<?> eventClass) {
		return consumerMap.get(eventClass);
	}

}
