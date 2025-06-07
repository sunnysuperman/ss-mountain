package com.sunnysuperman.mountain.mq.rocketmq4;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sunnysuperman.mountain.mq.MQAutoConfiguration;
import com.sunnysuperman.mountain.mq.MQConsumer;
import com.sunnysuperman.mountain.mq.MQProducer;

@Configuration(proxyBeanMethods = false)
@Import({ MQAutoConfiguration.class })
public class RocketMQ4AutoConfiguration {

	@Bean
	@ConditionalOnProperty("ss-mountain.mq.rocketmq4.producer.enabled")
	@ConfigurationProperties("ss-mountain.mq.rocketmq4.producer")
	public RocketMQ4ProducerProperties mqProducerProperties() {
		return new RocketMQ4ProducerProperties();
	}

	@Bean
	@ConditionalOnBean(RocketMQ4ProducerProperties.class)
	public MQProducer mqProducer(RocketMQ4ProducerProperties props) {
		return new RocketMQ4Producer(props);
	}

	@Bean
	@ConditionalOnProperty("ss-mountain.mq.rocketmq4.consumer.enabled")
	@ConfigurationProperties("ss-mountain.mq.rocketmq4.consumer")
	public RocketMQ4ConsumerProperties mqConsumerProperties() {
		return new RocketMQ4ConsumerProperties();
	}

	@Bean
	@ConditionalOnBean(RocketMQ4ConsumerProperties.class)
	public MQConsumer mqConsumer(RocketMQ4ConsumerProperties props) {
		return new RocketMQ4Consumer(props);
	}

}
