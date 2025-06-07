package com.sunnysuperman.mountain.mq;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public class MQProducerManager {
	private Map<String, MQProducer> producerMap = new ConcurrentHashMap<>();

	public MQProducer getProducer(String name) {
		return producerMap.get(name);
	}

	public synchronized void register(String name, MQProducer producer) {
		if (producerMap.put(name, producer) != null) {
			throw new UnexpectedException("Duplicate producer name: " + name);
		}
	}

	@PreDestroy
	public void shutdown() {
		producerMap.values().forEach(MQProducer::shutdown);
	}
}
