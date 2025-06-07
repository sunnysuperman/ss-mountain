package com.sunnysuperman.mountain.mq.rocketmq4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.ProcessUtil;
import com.sunnysuperman.mountain.lang.utils.Retryer;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.mq.MQConsumer;
import com.sunnysuperman.mountain.mq.MQException;
import com.sunnysuperman.mountain.mq.MessageKey;
import com.sunnysuperman.mountain.mq.MessageListenerManager;
import com.sunnysuperman.mountain.mq.MsgListener;

public class RocketMQ4Consumer implements MQConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(RocketMQ4Consumer.class);

	@Resource
	private EnvHelper envHelper;
	@Resource
	private MessageListenerManager listenerManager;
	private RocketMQ4ConsumerProperties props;
	private Consumer consumer;
	private volatile boolean available = true;

	public RocketMQ4Consumer(RocketMQ4ConsumerProperties props) {
		super();
		props.validate();
		this.props = props;
	}

	@Override
	public synchronized void start() {
		if (!available) {
			throw new MQException("Consumer is unavailable currently");
		}
		if (consumer != null) {
			return;
		}
		// 配置转换监听规则
		Map<String, Subscription> subscriptions = initSubscriptions();
		// 启动
		new Retryer().setMaxAttempts(3).call(() -> {
			// 创建consumer实例
			Properties p = new Properties();
			p.put(PropertyKeyConst.AccessKey, props.getAccessKey());
			p.put(PropertyKeyConst.SecretKey, props.getAccessSecret());
			p.put(PropertyKeyConst.NAMESRV_ADDR, props.getEndpoints());
			p.put(PropertyKeyConst.SuspendTimeMillis, props.getSuspendTimeMillis());
			p.put(PropertyKeyConst.MaxReconsumeTimes, props.getMaxReconsumeTimes());
			p.put(PropertyKeyConst.ConsumeTimeout, props.getConsumeTimeout());
			p.put(PropertyKeyConst.GROUP_ID, props.getGroup());
			p.put(PropertyKeyConst.MessageModel,
					props.isBroadcasting() || envHelper.isLocal() ? PropertyValueConst.BROADCASTING
							: PropertyValueConst.CLUSTERING);
			Consumer c = ONSFactory.createConsumer(p);
			// 启动
			doStart(c, p, subscriptions.values());
			// 设置consumer
			consumer = c;
			return null;
		});
	}

	private Map<String, Subscription> initSubscriptions() {
		Map<String, Subscription> subscriptions = new HashMap<>(props.getQueues().size());
		for (RocketMQ4QueueProperties queue : props.getQueues()) {
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
				mergeSubscription(subscription, queue, expression, listenersByRealTag);
			}
		}
		return subscriptions;
	}

	private void mergeSubscription(Subscription subscription, RocketMQ4QueueProperties queue, String expression,
			Map<String, MsgListener> listenersByRealTag) {
		if (Str.isEmpty(queue.getTagPrefix())) {
			throw new MQException(
					"Require tagPrefix if set the same topic '" + queue.getTopic() + "' on multiple queues");
		}
		// 对同一个topic有多个监听列队
		// 合并监听规则
		subscription.subExpression = subscription.subExpression + "||" + expression;
		// 合并监听者
		MessageListenerByQueue listener = new MessageListenerByQueue(queue, listenersByRealTag);
		if (subscription.listener instanceof MessageListenerByQueue) {
			List<MessageListenerByQueue> listeners = new ArrayList<>(2);
			listeners.add((MessageListenerByQueue) subscription.listener);
			listeners.add(listener);
			subscription.listener = new MessageListenerByQueues(listeners);
		} else {
			MessageListenerByQueues listeners = (MessageListenerByQueues) subscription.listener;
			listeners.listeners.add(listener);
		}
	}

	private void doStart(Consumer c, Properties p, Collection<Subscription> subscriptions) throws MQException {
		try {
			subscriptions.forEach(sub -> c.subscribe(sub.topic, sub.subExpression, sub.listener));
			c.start();
			if (LOG.isInfoEnabled()) {
				Map<Object, Object> logProps = new HashMap<>(p);
				logProps.remove(PropertyKeyConst.SecretKey);
				if (LOG.isInfoEnabled()) {
					LOG.info(">>>>>>[mq-rocketmq4] consumer initialized, props:{}, queues:{}, subscriptions:{}",
							logProps, props.getQueues(), subscriptions);
				}
			}
		} catch (Exception ex) {
			doShutdown(c);
			throw MQException.wrap(ex);
		}
	}

	@Override
	public synchronized void shutdown() {
		if (!available) {
			return;
		}
		available = false;
		doShutdown(consumer);
		consumer = null;
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

		@Override
		public String toString() {
			return Jsons.write(Map.of("topic", topic, "subExpression", subExpression));
		}
	}

	private void doShutdown(Consumer c) {
		if (c != null) {
			try {
				c.shutdown();
			} catch (Exception e) {
				LOG.error(null, e);
			}
		}
	}

	private class MessageListenerByQueues implements MessageListener {
		List<MessageListenerByQueue> listeners;

		public MessageListenerByQueues(List<MessageListenerByQueue> listeners) {
			super();
			this.listeners = listeners;
		}

		@Override
		public Action consume(Message message, ConsumeContext context) {
			for (MessageListenerByQueue listener : listeners) {
				if (listener.listenerMap.containsKey(message.getTag())) {
					return listener.consume(message, context);
				}
			}
			LOG.error("[MQ] No message listener found for tag: {}", message.getTag());
			return Action.ReconsumeLater;
		}

	}

	private class MessageListenerByQueue implements MessageListener {
		private RocketMQ4QueueProperties queue;
		private Map<String, MsgListener> listenerMap;

		public MessageListenerByQueue(RocketMQ4QueueProperties queue, Map<String, MsgListener> listenerMap) {
			super();
			this.queue = queue;
			this.listenerMap = listenerMap;
		}

		@Override
		public Action consume(Message message, ConsumeContext context) {
			if (queue.isVerboseLog() && LOG.isInfoEnabled()) {
				LOG.info("[MQ] Receive message: {}", message);
			}
			if (!available) {
				return Action.ReconsumeLater;
			}
			if (envHelper.isLocal()) {
				String localMac = ProcessUtil.ensureLocalMacAddress();
				String messageMac = message.getUserProperties() != null
						? message.getUserProperties(MessageKey.MAC.getKey())
						: null;
				if (!localMac.equals(messageMac)) {
					if (LOG.isInfoEnabled()) {
						LOG.info("[MQ] Ignore message from machine {} ",
								message.getUserProperties(MessageKey.MAC.getKey()));
					}
					return Action.CommitMessage;
				}
			}
			MsgListener listener = listenerMap.get(message.getTag());
			if (listener == null) {
				LOG.error("[MQ] No message listener found for tag: {}", message.getTag());
				return Action.ReconsumeLater;
			}
			boolean ok = false;
			try {
				ok = listener.onMessage(message.getBody(), message.getUserProperties());
			} catch (Exception e) {
				LOG.error("[MQ] Process message error: " + message.toString(), e);
			}
			return ok ? Action.CommitMessage : Action.ReconsumeLater;
		}

	}

}
