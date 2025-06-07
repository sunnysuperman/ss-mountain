package com.sunnysuperman.mountain.base;

import java.util.Collection;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ComponentManager implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@SuppressWarnings("squid:S2696")
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	public static Object get(String name) {
		return applicationContext.getBean(name);
	}

	public static <T> T get(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	public static <T> Collection<T> findForList(Class<T> type) {
		return applicationContext.getBeansOfType(type).values();
	}

}
