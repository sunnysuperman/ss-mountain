package com.sunnysuperman.mountain.mq.rocketmq5;

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
public class RocketMQ5AutoConfiguration {

	@Bean
	@ConditionalOnProperty("ss-mountain.mq.rocketmq5.producer.enabled")
	@ConfigurationProperties("ss-mountain.mq.rocketmq5.producer")
	public RocketMQ5ProducerProperties mqProducerProperties() {
		return new RocketMQ5ProducerProperties();
	}

	@Bean
	@ConditionalOnBean(RocketMQ5ProducerProperties.class)
	public MQProducer mqProducer(RocketMQ5ProducerProperties props) {
		return new RocketMQ5Producer(props);
	}

	@Bean
	@ConditionalOnProperty("ss-mountain.mq.rocketmq5.consumer.enabled")
	@ConfigurationProperties("ss-mountain.mq.rocketmq5.consumer")
	public RocketMQ5ConsumerProperties mqConsumerProperties() {
		return new RocketMQ5ConsumerProperties();
	}

	@Bean
	@ConditionalOnBean(RocketMQ5ConsumerProperties.class)
	public MQConsumer mqConsumer(RocketMQ5ConsumerProperties props) {
		return new RocketMQ5Consumer(props);
	}

}
