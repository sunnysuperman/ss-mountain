package com.sunnysuperman.mountain.mq.rocketmq5;

import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.ProducerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.Retryer;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lang.utils.Types;
import com.sunnysuperman.mountain.mq.MQException;
import com.sunnysuperman.mountain.mq.MQProduceOptions;
import com.sunnysuperman.mountain.mq.MQProducer;
import com.sunnysuperman.mountain.mq.MQProducerManager;

public class RocketMQ5Producer implements MQProducer {
	private static final Logger LOG = LoggerFactory.getLogger(RocketMQ5Producer.class);

	@Resource
	private MQProducerManager producerManager;
	private RocketMQ5ProducerProperties props;
	private Producer producer;

	public RocketMQ5Producer(RocketMQ5ProducerProperties props) {
		this.props = props;
	}

	@PostConstruct
	public void init() {
		// 校验
		props.validate();
		props.getQueues().forEach(queue -> producerManager.register(queue.getName(), this));
		// 启动生产者
		if (!props.isLazyInit()) {
			start();
		}
	}

	private synchronized void start() throws MQException {
		if (producer != null) {
			return;
		}
		try {
			final ClientServiceProvider provider = ClientServiceProvider.loadService();
			SessionCredentialsProvider sessionCredentialsProvider = new StaticSessionCredentialsProvider(
					props.getAccessKey(), props.getAccessSecret());
			ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
					.setEndpoints(props.getEndpoints()).setCredentialProvider(sessionCredentialsProvider).build();
			final ProducerBuilder builder = provider.newProducerBuilder().setClientConfiguration(clientConfiguration)
					.setTopics(
							props.getQueues().stream().map(RocketMQ5QueueProperties::getTopic).toArray(String[]::new));
			producer = builder.build();
			if (LOG.isInfoEnabled()) {
				LOG.info("============RocketMQ5Producer started============\n{}", props);
			}
		} catch (Exception ex) {
			shutdown();
			throw new MQException("Failed to init producer", ex);
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param content 消息内容(可以二进制内容、字符串或POJO等)
	 * @param options 消息发送选项
	 */
	@Override
	public void produce(String queueName, Object content, MQProduceOptions options) throws MQException {
		// 先初始化(如果没有初始化的话)
		if (producer == null) {
			init();
		}
		RocketMQ5QueueProperties queue = getQueue(queueName);
		byte[] contentAsBytes = serializeContent(content);
		// 生成消息
		Message msg = makeMessage(queue, contentAsBytes, options);
		// 同步 OR 异步
		boolean sync = options.getSync() != null ? options.getSync() : props.isSync();
		// 重试次数
		int maxAttempts = options.getMaxAttempts() > 0 ? options.getMaxAttempts() : props.getMaxAttempts();
		// 发送
		if (queue.isVerboseLog() && LOG.isInfoEnabled()) {
			LOG.info("[MQ] Send message: {}, sync: {}, maxAttempts: {}", msg, sync, maxAttempts);
		}
		if (maxAttempts <= 1) {
			doSend(msg, contentAsBytes, sync, queue.isVerboseLog());
		} else {
			try {
				new Retryer().setLogger(LOG).setMaxAttempts(maxAttempts).call(() -> {
					doSend(msg, contentAsBytes, sync, queue.isVerboseLog());
					return null;
				});
			} catch (MQException e) {
				throw e;
			} catch (Exception ex) {
				throw new MQException(ex);
			}
		}
	}

	private void doSend(Message msg, byte[] contentAsBytes, boolean sync, boolean verboseLog) throws MQException {
		try {
			if (sync) {
				String msgId = producer.send(msg).getMessageId().toString();
				if (verboseLog && LOG.isInfoEnabled()) {
					LOG.info("[MQ] Message sent: {}, {}", msgId, new String(contentAsBytes, Str.UTF8_CHARSET));
				}
			} else {
				producer.sendAsync(msg);
			}
		} catch (Exception e) {
			throw new MQException("Failed to send", e);
		}
	}

	protected byte[] serializeContent(Object content) {
		Class<?> contentType = content.getClass();
		if (content instanceof String) {
			return ((String) content).getBytes(Str.UTF8_CHARSET);
		} else if (Types.isByteArray(contentType)) {
			return (byte[]) content;
		} else {
			return Jsons.writeAsBytes(content);
		}
	}

	private RocketMQ5QueueProperties getQueue(String queueName) throws MQException {
		for (RocketMQ5QueueProperties queue : props.getQueues()) {
			if (queue.getName().equals(queueName)) {
				return queue;
			}
		}
		throw new MQException("No queue named: " + queueName);
	}

	private Message makeMessage(RocketMQ5QueueProperties queue, byte[] content, MQProduceOptions options)
			throws MQException {
		// 构造消息
		ClientServiceProvider provider = ClientServiceProvider.loadService();
		String topic = queue.getTopic();
		String tag = Str.isNotEmpty(queue.getTagPrefix()) ? queue.getTagPrefix() + options.getTag() : options.getTag();
		MessageBuilder msgBuilder = provider.newMessageBuilder().setTopic(topic).setTag(tag).setBody(content);
		// 设置发送时间
		Date deliveryTime = options.getDeliveryTime();
		if (deliveryTime != null) {
			msgBuilder.setDeliveryTimestamp(deliveryTime.getTime());
		}
		// 设置消息头
		Properties headers = options.getHeaders();
		if (headers != null) {
			headers.forEach((k, v) -> {
				String key = k.toString();
				String value = v.toString();
				if (key.equals("__KEY")) {
					msgBuilder.setKeys(value);
				}
				msgBuilder.addProperty(key, value);
			});
		}
		return msgBuilder.build();
	}

	@Override
	public void shutdown() {
		if (producer != null) {
			try {
				producer.close();
			} catch (Exception e) {
				LOG.error(null, e);
			}
		}
	}

}
