package com.sunnysuperman.mountain.base;

public class ComponentHolder<T> {

	Class<T> type;
	T component;

	public ComponentHolder(Class<T> type) {
		super();
		this.type = type;
	}

	public T get() {
		if (component == null) {
			component = ComponentManager.get(type);
		}
		return component;
	}

}
