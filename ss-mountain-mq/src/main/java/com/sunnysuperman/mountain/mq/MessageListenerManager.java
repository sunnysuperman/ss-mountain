package com.sunnysuperman.mountain.mq;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import com.sunnysuperman.mountain.lang.concurrent.BooleanLock;
import com.sunnysuperman.mountain.lang.utils.Str;

public class MessageListenerManager implements BeanPostProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(MessageListenerManager.class);
	private Map<String, Map<String, MsgListener>> listenerMap = new HashMap<>();
	private BooleanLock initialized = new BooleanLock(false);

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// 注册监听器
		Method[] methods = bean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.getAnnotation(MessageListener.class) != null) {
				try {
					registerListener(bean, method);
				} catch (MQException e) {
					throw new MQException(e.getMessage() + " at " + method, e.getCause());
				}
			}
		}
		return bean;
	}

	@EventListener(ApplicationReadyEvent.class)
	@Order(100000)
	public void onReady() {
		initialized.setValue(true);
		LOG.info(">>>>>>[mq] {} message queue(s) registered", listenerMap.size());
	}

	public Map<String, MsgListener> useListeners(String queueName) {
		return listenerMap.remove(queueName);
	}

	public Set<String> getNotUsedQueues() {
		return listenerMap.keySet();
	}

	/** 注册消息处理器 **/
	public void registerListener(String queue, String tag, MsgListener listener) throws MQException {
		doRegisterListener(queue, tag, listener);
	}

	private void registerListener(Object bean, Method method) throws MQException {
		Class<?>[] parameters = method.getParameterTypes();
		if (parameters.length > 1) {
			throw new MQException("Message listener method should accept one parameter at most");
		}
		if (method.getReturnType() != boolean.class && method.getReturnType() != void.class) {
			throw new MQException("Message listener method should return boolean or void");
		}
		MessageListener cfg = method.getAnnotation(MessageListener.class);
		doRegisterListener(cfg.queue(), cfg.tag(),
				new TheMQConsumeListener(bean, method, parameters.length > 0 ? parameters[0] : null));
	}

	private synchronized void doRegisterListener(String queue, String tag, MsgListener listener) throws MQException {
		if (initialized.isTrue()) {
			throw new MQException("Could not register listener after consumer manager initialized.");
		}
		if (Str.isEmpty(queue)) {
			throw new MQException("Message queue is empty");
		}
		if (Str.isEmpty(tag)) {
			throw new MQException("Message tag is empty");
		}
		Map<String, MsgListener> listenersByTag = listenerMap.computeIfAbsent(queue, k -> new HashMap<>());
		if (listenersByTag.putIfAbsent(tag, listener) != null) {
			throw new MQException("Duplicate message tag '" + tag + "'");
		}
	}

	private static class TheMQConsumeListener extends TypedMsgListener {
		Object bean;
		Method method;
		Class<?> msgType;

		public TheMQConsumeListener(Object bean, Method method, Class<?> msgType) {
			super();
			this.bean = bean;
			this.method = method;
			this.msgType = msgType;
		}

		@Override
		protected Class<?> castType() {
			return msgType;
		}

		@Override
		protected boolean onCastMessage(Object msg, Properties headers) {
			Object result;
			try {
				if (msgType == null) {
					result = method.invoke(bean);
				} else {
					result = method.invoke(bean, msg);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new MQException(e);
			}
			return !Objects.equals(result, Boolean.FALSE);
		}

	}
}
