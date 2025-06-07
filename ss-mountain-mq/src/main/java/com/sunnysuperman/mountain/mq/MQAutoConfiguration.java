package com.sunnysuperman.mountain.mq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MQAutoConfiguration {

	@Bean
	public MessageListenerManager messageListenerManager() {
		return new MessageListenerManager();
	}

	@Bean
	public MQConsumerManager mqConsumerManager(MessageListenerManager messageListenerManager) {
		return new MQConsumerManager(messageListenerManager);
	}

	@Bean
	public MQProducerManager mqProducerManager() {
		return new MQProducerManager();
	}

}
