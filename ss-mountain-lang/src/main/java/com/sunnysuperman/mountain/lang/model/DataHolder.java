package com.sunnysuperman.mountain.lang.model;

public class DataHolder<T> {
	T data;

	public DataHolder() {
		super();
	}

	public DataHolder(T data) {
		super();
		this.data = data;
	}

	public T get() {
		return data;
	}

	public void set(T data) {
		this.data = data;
	}

}