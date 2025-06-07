package com.sunnysuperman.mountain.mq.rocketmq4.test;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sunnysuperman.mountain.mq.MessageListener;

@Component
public class MyService {
	private static final Logger LOG = LoggerFactory.getLogger(MyService.class);

	Map<String, Boolean> consumedMessages = new ConcurrentHashMap<String, Boolean>();

	@MessageListener(tag = DefaultMessage.TAG)
	public boolean onMessage(DefaultMessage msg) {
		LOG.info(">>>DefaultMessage consumed: {}", msg.getContent());
		consumedMessages.put(Objects.requireNonNull(msg.getContent()), Boolean.TRUE);
		return true;
	}

	@MessageListener(queue = "mm", tag = MMMessage.TAG)
	public boolean onMMMessage(MMMessage msg) {
		LOG.info(">>>MMMessage consumed: {}", msg.getMmText());
		consumedMessages.put(Objects.requireNonNull(msg.getMmText()), Boolean.TRUE);
		return true;
	}

	@MessageListener(queue = "m2", tag = M2Message.TAG)
	public boolean onM2Message(M2Message msg) {
		LOG.info(">>>M2Message consumed: {}", msg.getText());
		consumedMessages.put(Objects.requireNonNull(msg.getText()), Boolean.TRUE);
		return true;
	}

	@MessageListener(queue = "m2", tag = M2Message2.TAG)
	public boolean onAnotherM2Message(M2Message2 msg) {
		LOG.info(">>>M2Message2 consumed: {}", msg.getPayload());
		consumedMessages.put(Objects.requireNonNull(msg.getPayload()), Boolean.TRUE);
		return true;
	}

	public Set<String> getConsumedMessages() {
		return new HashSet<>(consumedMessages.keySet());
	}
}
