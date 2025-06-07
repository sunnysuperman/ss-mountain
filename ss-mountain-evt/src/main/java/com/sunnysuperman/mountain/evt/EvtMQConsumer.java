package com.sunnysuperman.mountain.evt;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.mq.MessageListenerManager;
import com.sunnysuperman.mountain.mq.TypedMsgListener;

/** 事件MQ消费管理 **/
public class EvtMQConsumer {
	private static final Logger LOG = LoggerFactory.getLogger(EvtMQConsumer.class);
	private EvtConsumer evtConsumer;
	private EvtClassManager evtClassMananger;
	private MessageListenerManager messageListenerManager;

	public EvtMQConsumer(EvtConsumer evtConsumer, EvtClassManager evtClassMananger,
			MessageListenerManager messageListenerManager) {
		super();
		this.evtConsumer = evtConsumer;
		this.evtClassMananger = evtClassMananger;
		this.messageListenerManager = messageListenerManager;
	}

	@EventListener(ApplicationReadyEvent.class)
	@Order(100)
	public void onReady() {
		Set<Class<?>> classes = evtClassMananger.getAll();
		Set<String> tags = new HashSet<>(classes.size());
		for (Class<?> clazz : classes) {
			EvtConf conf = clazz.getAnnotation(EvtConf.class);
			if (!conf.mqDisabled()) {
				String tag = Str.or(conf.mqTag(), conf.name());
				if (Str.isEmpty(tag)) {
					throw new UnexpectedException("Empty message tag of event: " + clazz);
				}
				if (!tags.add(tag)) {
					throw new UnexpectedException("Duplicate event message tag '" + tag + "'");
				}
				// 注册消费者
				messageListenerManager.registerListener(conf.mqName(), tag, new EvtMQConsumeListener(clazz));
			}
		}
		LOG.info(">>>>>>[evt] message listener registered: {}", tags);
		tags.clear();
	}

	private class EvtMQConsumeListener extends TypedMsgListener {
		Class<?> evtClass;

		public EvtMQConsumeListener(Class<?> evtClass) {
			super();
			this.evtClass = evtClass;
		}

		@Override
		protected Class<?> castType() {
			return evtClass;
		}

		@Override
		protected boolean onCastMessage(Object msg, Properties headers) {
			return evtConsumer.consume((Evt) msg);
		}

	}

}
