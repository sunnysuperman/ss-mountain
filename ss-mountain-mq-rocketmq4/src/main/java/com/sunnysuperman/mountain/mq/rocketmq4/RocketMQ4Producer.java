package com.sunnysuperman.mountain.mq.rocketmq4;

import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.ProcessUtil;
import com.sunnysuperman.mountain.lang.utils.Retryer;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lang.utils.Types;
import com.sunnysuperman.mountain.mq.MQException;
import com.sunnysuperman.mountain.mq.MQProduceOptions;
import com.sunnysuperman.mountain.mq.MQProducer;
import com.sunnysuperman.mountain.mq.MQProducerManager;
import com.sunnysuperman.mountain.mq.MessageKey;

public class RocketMQ4Producer implements MQProducer {
	private static final Logger LOG = LoggerFactory.getLogger(RocketMQ4Producer.class);

	@Resource
	private EnvHelper envHelper;
	@Resource
	private MQProducerManager producerManager;
	private RocketMQ4ProducerProperties props;
	private Producer producer;
	private volatile boolean available = true;

	public RocketMQ4Producer(RocketMQ4ProducerProperties props) {
		this.props = props;
	}

	@PostConstruct
	public synchronized void init() throws MQException {
		// 校验
		props.validate();
		props.getQueues().forEach(queue -> producerManager.register(queue.getName(), this));
		// 启动生产者
		if (!props.isLazyInit()) {
			start();
		}
		if (LOG.isInfoEnabled()) {
			LOG.info(">>>>>>[mq-rocketmq4] producer initialized: {}", props);
		}
	}

	private synchronized void start() throws MQException {
		ensureAvailable();
		if (producer != null) {
			return;
		}
		try {
			Properties p = new Properties();
			p.put(PropertyKeyConst.AccessKey, props.getAccessKey());
			p.put(PropertyKeyConst.SecretKey, props.getAccessSecret());
			p.put(PropertyKeyConst.NAMESRV_ADDR, props.getEndpoints());
			p.put(PropertyKeyConst.SendMsgTimeoutMillis, String.valueOf(props.getSendMsgTimeoutMillis()));
			producer = ONSFactory.createProducer(p);
			producer.start();
			if (LOG.isInfoEnabled()) {
				LOG.info(">>>>>>[mq-rocketmq4] producer started");
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
		ensureAvailable();
		// 先初始化(如果没有初始化的话)
		if (producer == null) {
			start();
		}
		RocketMQ4QueueProperties queue = getQueue(queueName);
		// 生成消息
		Message msg = makeMessage(queue, serializeContent(content), options);
		// 同步 OR 异步
		boolean sync = options.getSync() != null ? options.getSync() : props.isSync();
		// 重试次数
		int maxAttempts = options.getMaxAttempts() > 0 ? options.getMaxAttempts() : props.getMaxAttempts();
		// 发送
		if (queue.isVerboseLog() && LOG.isInfoEnabled()) {
			LOG.info("[MQ] Send message: {}, sync: {}, maxAttempts: {}", new String(msg.getBody()), sync, maxAttempts);
		}
		if (maxAttempts <= 1) {
			doSend(msg, sync, queue.isVerboseLog());
		} else {
			try {
				new Retryer().setLogger(LOG).setMaxAttempts(maxAttempts).call(() -> {
					doSend(msg, sync, queue.isVerboseLog());
					return null;
				});
			} catch (MQException e) {
				throw e;
			} catch (Exception ex) {
				throw new MQException(ex);
			}
		}
	}

	private void doSend(Message msg, boolean sync, boolean verboseLog) throws MQException {
		try {
			if (sync) {
				String msgId = producer.send(msg).getMessageId();
				if (verboseLog && LOG.isInfoEnabled()) {
					LOG.info("[MQ] Message sent: {}, {}", msgId, new String(msg.getBody(), Str.UTF8_CHARSET));
				}
			} else {
				producer.sendOneway(msg);
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

	private RocketMQ4QueueProperties getQueue(String queueName) throws MQException {
		for (RocketMQ4QueueProperties queue : props.getQueues()) {
			if (queue.getName().equals(queueName)) {
				return queue;
			}
		}
		throw new MQException("No queue named: " + queueName);
	}

	private Message makeMessage(RocketMQ4QueueProperties queue, byte[] content, MQProduceOptions options)
			throws MQException {
		// 构造消息
		String topic = queue.getTopic();
		String tag = Str.isNotEmpty(queue.getTagPrefix()) ? queue.getTagPrefix() + options.getTag() : options.getTag();
		Message msg = new Message(topic, tag, content);
		// 设置发送时间
		Date deliveryTime = options.getDeliveryTime();
		if (deliveryTime != null) {
			msg.setStartDeliverTime(deliveryTime.getTime());
		}
		// 设置消息头
		Properties headers = options.getHeaders();
		if (envHelper.isLocal()) {
			if (headers == null) {
				headers = new Properties();
			}
			headers.setProperty(MessageKey.MAC.getKey(), ProcessUtil.ensureLocalMacAddress());
		}
		if (headers != null) {
			msg.setUserProperties(headers);
		}
		return msg;
	}

	@Override
	public synchronized void shutdown() {
		if (!available) {
			return;
		}
		available = false;
		if (producer != null) {
			try {
				producer.shutdown();
			} catch (Exception e) {
				LOG.error(null, e);
			}
			producer = null;
		}
	}

	private void ensureAvailable() {
		if (!available) {
			throw new MQException("Producer is unavailable currently");
		}
	}

}
