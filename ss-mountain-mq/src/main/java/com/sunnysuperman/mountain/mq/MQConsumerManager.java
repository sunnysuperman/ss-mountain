package com.sunnysuperman.mountain.mq;

import java.util.Collection;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.sunnysuperman.mountain.base.ComponentManager;
import com.sunnysuperman.mountain.lang.utils.Str;

public class MQConsumerManager {
	private static final Logger LOG = LoggerFactory.getLogger(MQConsumerManager.class);
	private MessageListenerManager listenerManager;

	public MQConsumerManager(MessageListenerManager listenerManager) {
		super();
		this.listenerManager = listenerManager;
	}

	@EventListener(ApplicationReadyEvent.class)
	@Order(Ordered.LOWEST_PRECEDENCE)
	public void onReady(ApplicationReadyEvent e) {
		Collection<MQConsumer> consumers = ComponentManager.findForList(MQConsumer.class);
		// 启动消费者
		for (MQConsumer consumer : consumers) {
			consumer.start();
		}
		// 判断是否所有消息监听器都绑定到消息列队
		Set<String> notUsedQueues = listenerManager.getNotUsedQueues();
		if (!notUsedQueues.isEmpty()) {
			throw new MQException("No consumer found for queue [" + Str.join(notUsedQueues)
					+ "], consider registering message listener or adding mq dependency to class path");
		}
		LOG.info(">>>>>>[mq] {} consumers started", consumers.size());
	}

	@PreDestroy
	public void shutdown() {
		// 关闭消费者
		Collection<MQConsumer> consumers = ComponentManager.findForList(MQConsumer.class);
		for (MQConsumer consumer : consumers) {
			consumer.shutdown();
		}
	}
}
