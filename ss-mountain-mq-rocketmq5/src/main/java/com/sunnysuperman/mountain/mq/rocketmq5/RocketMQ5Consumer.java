package com.sunnysuperman.mountain.mq.rocketmq5;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.consumer.PushConsumerBuilder;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.Retryer;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.mq.MQConsumer;
import com.sunnysuperman.mountain.mq.MQException;
import com.sunnysuperman.mountain.mq.MessageListenerManager;
import com.sunnysuperman.mountain.mq.MsgListener;

public class RocketMQ5Consumer implements MQConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(RocketMQ5Consumer.class);

	@Resource
	private MessageListenerManager listenerManager;
	private RocketMQ5ConsumerProperties props;
	private PushConsumer consumer;
	private boolean running = true;

	public RocketMQ5Consumer(RocketMQ5ConsumerProperties props) {
		super();
		props.validate();
		this.props = props;
	}

	@Override
	public synchronized void start() throws MQException {
		if (consumer != null) {
			throw new MQException("Consumer already started");
		}
		try {
			// 配置转换监听规则
			Map<String, Subscription> subscriptions = wrapSubscriptions();
			// 创建consumer实例
			new Retryer().setMaxAttempts(3).call(() -> {
				final ClientServiceProvider provider = ClientServiceProvider.loadService();
				SessionCredentialsProvider sessionCredentialsProvider = new StaticSessionCredentialsProvider(
						props.getAccessKey(), props.getAccessSecret());
				ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
						.setEndpoints(props.getEndpoints()).setCredentialProvider(sessionCredentialsProvider).build();
				PushConsumerBuilder builder = provider.newPushConsumerBuilder()
						.setClientConfiguration(clientConfiguration).setConsumerGroup(props.getGroup());
				Map<String, FilterExpression> filterExpression = subscriptions.entrySet().stream()
						.collect(Collectors.toMap(Entry::getKey,
								i -> new FilterExpression(i.getValue().subExpression, FilterExpressionType.TAG)));
				builder.setSubscriptionExpressions(filterExpression);
				builder.setMessageListener(
						messageView -> subscriptions.get(messageView.getTopic()).listener.consume(messageView));
				consumer = builder.build();
				return null;
			});
		} catch (Exception ex) {
			throw MQException.wrap(ex);
		}
	}

	@Override
	public synchronized void shutdown() {
		running = false;
		if (consumer != null) {
			try {
				consumer.close();
			} catch (Exception e) {
				LOG.error(null, e);
			}
			consumer = null;
		}
	}

	private Map<String, Subscription> wrapSubscriptions() {
		Map<String, Subscription> subscriptions = new HashMap<>(props.getQueues().size());
		for (RocketMQ5QueueProperties queue : props.getQueues()) {
			Map<String, MsgListener> listenersByTag = listenerManager.useListeners(queue.getName());
			if (listenersByTag == null) {
				throw new MQException("No listeners of message queue: " + queue.toString());
			}
			String expression = Str.join(Str.isEmpty(queue.getTagPrefix()) ? listenersByTag.keySet()
					: listenersByTag.keySet().stream().map(i -> queue.getTagPrefix() + i).collect(Collectors.toSet()),
					"||");
			Map<String, MsgListener> listenersByRealTag = Str.isEmpty(queue.getTagPrefix()) ? listenersByTag
					: listenersByTag.entrySet().stream().collect(
							Collectors.toMap(entry -> queue.getTagPrefix() + entry.getKey(), Map.Entry::getValue));
			String topic = queue.getTopic();
			Subscription subscription = subscriptions.get(topic);
			if (subscription == null) {
				subscription = new Subscription(topic, expression,
						new MessageListenerByQueue(queue, listenersByRealTag));
				subscriptions.put(topic, subscription);
			} else {
				if (Str.isEmpty(queue.getTagPrefix())) {
					throw new MQException("Require tagPrefix if set the same topic '" + topic + "' on multiple queues");
				}
				// 对同一个topic有多个监听列队
				// 合并监听规则
				subscription.subExpression = subscription.subExpression + "||" + expression;
				// 合并监听者
				MessageListenerByQueue listener = new MessageListenerByQueue(queue, listenersByRealTag);
				subscription.addListener(listener);
			}
		}
		return subscriptions;
	}

	private static class Subscription {
		String topic;
		String subExpression;
		MessageListener listener;

		public Subscription(String topic, String subExpression, MessageListener listener) {
			super();
			this.topic = topic;
			this.subExpression = subExpression;
			this.listener = listener;
		}

		public void addListener(MessageListenerByQueue l) {
			if (listener instanceof MessageListenerByQueue) {
				List<MessageListenerByQueue> listeners = new ArrayList<>(2);
				listeners.add((MessageListenerByQueue) listener);
				listeners.add(l);
				listener = new MessageListenerByQueues(listeners);
			} else {
				MessageListenerByQueues listeners = (MessageListenerByQueues) listener;
				listeners.listeners.add(l);
			}
		}

		@Override
		public String toString() {
			return Jsons.write(Map.of("topic", topic, "subExpression", subExpression));
		}
	}

	private static class MessageListenerByQueues implements MessageListener {
		List<MessageListenerByQueue> listeners;

		public MessageListenerByQueues(List<MessageListenerByQueue> listeners) {
			super();
			this.listeners = listeners;
		}

		@Override
		public ConsumeResult consume(MessageView messageView) {
			for (MessageListenerByQueue listener : listeners) {
				if (listener.listenerMap.containsKey(getTag(messageView))) {
					return listener.consume(messageView);
				}
			}
			LOG.error("[MQ] No message listener found for tag: {}", messageView.getTag());
			return ConsumeResult.FAILURE;
		}

	}

	private class MessageListenerByQueue implements MessageListener {
		private RocketMQ5QueueProperties queue;
		private Map<String, MsgListener> listenerMap;

		public MessageListenerByQueue(RocketMQ5QueueProperties queue, Map<String, MsgListener> listenerMap) {
			super();
			this.queue = queue;
			this.listenerMap = listenerMap;
		}

		@Override
		public ConsumeResult consume(MessageView messageView) {
			if (queue.isVerboseLog() && LOG.isInfoEnabled()) {
				LOG.info("[MQ] Receive message: {}", message2String(messageView));
			}
			if (!running) {
				return ConsumeResult.FAILURE;
			}
			MsgListener listener = listenerMap.get(getTag(messageView));
			if (listener == null) {
				LOG.error("[MQ] No message listener found for tag: {}", messageView.getTag());
				return ConsumeResult.FAILURE;
			}
			boolean ok = false;
			try {
				ok = listener.onMessage(byteBuffer2ByteArray(messageView.getBody()), null);
			} catch (Exception e) {
				LOG.error("[MQ] Process message error: " + message2String(messageView), e);
			}
			return ok ? ConsumeResult.SUCCESS : ConsumeResult.FAILURE;
		}

	}

	private static String getTag(MessageView messageView) {
		return messageView.getTag().orElse(null);
	}

	private static String message2String(MessageView messageView) {
		return byteBuffer2String(messageView.getBody());
	}

	private static String byteBuffer2String(ByteBuffer buffer) {
		CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
		return charBuffer.toString();
	}

	private static byte[] byteBuffer2ByteArray(ByteBuffer buffer) {
		byte[] bytes;
		if (buffer.hasArray()) {
			bytes = buffer.array();
			int offset = buffer.arrayOffset();
			int length = buffer.remaining();
			bytes = Arrays.copyOfRange(bytes, offset, offset + length);
		} else {
			bytes = new byte[buffer.remaining()];
			buffer.position(0);
			buffer.get(bytes);
		}
		return bytes;
	}

}
