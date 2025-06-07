package com.sunnysuperman.mountain.evt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.sunnysuperman.mountain.lang.concurrent.Flag;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;

public class EvtListenerManager implements BeanPostProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(EvtListenerManager.class);
	private Map<Class<? extends Evt>, List<Listener>> listenerMap = new HashMap<>();
	private Flag initialized = new Flag(EvtListenerManager.class, LOG);

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// 注册监听器
		Method[] methods = bean.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.getAnnotation(EvtListener.class) != null) {
				registerListener(bean, method);
			}
		}
		return bean;
	}

	@EventListener(ApplicationReadyEvent.class)
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public void ready() {
		initialized.setAsTrue();
	}

	public List<Listener> getListeners(Class<? extends Evt> evtClass) throws EvtException {
		if (!initialized.waitUntilTrue(10000)) {
			throw new EvtException("未初始化事件监听者");
		}
		List<Listener> listeners = listenerMap.get(evtClass);
		if (listeners == null) {
			throw new EvtException("未找到事件监听者: " + evtClass);
		}
		return listeners;
	}

	private void registerListener(Object bean, Method method) {
		if (initialized.isTrue()) {
			throw new UnexpectedException("初始化之后不能再添加事件监听器");
		}
		if (!Modifier.isPublic(method.getModifiers())) {
			throw new UnexpectedException("事件监听方法需要为public");
		}
		if (method.getParameterTypes().length != 1 || !Evt.class.isAssignableFrom(method.getParameterTypes()[0])) {
			throw new UnexpectedException("事件监听方法需要添加事件入参");
		}
		if (method.getReturnType() != boolean.class && method.getReturnType() != void.class) {
			throw new UnexpectedException("事件监听方法需返回boolean|void类型");
		}
		EvtListener listener = method.getAnnotation(EvtListener.class);
		Class<?>[] evtClasses = listener.value();
		if (evtClasses.length == 0) {
			evtClasses = new Class<?>[] { method.getParameters()[0].getType() };
		}
		for (Class<?> evtClass : evtClasses) {
			@SuppressWarnings("unchecked")
			List<Listener> listeners = listenerMap.computeIfAbsent((Class<? extends Evt>) evtClass,
					k -> new ArrayList<>(1));
			listeners.add(new Listener(bean, method, listener.order()));
			if (listeners.size() > 1) {
				Collections.sort(listeners, (o1, o2) -> o1.order - o2.order);
			}
		}
	}

	public static class Listener {
		Object bean;
		Method method;
		int order;

		public Listener(Object bean, Method method, int order) {
			super();
			this.bean = bean;
			this.method = method;
			this.order = order;
		}

		public boolean consume(Evt evt) {
			try {
				Object result = method.invoke(bean, evt);
				return !Objects.equals(result, Boolean.FALSE);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new EvtException(evt, e);
			}
		}
	}
}
