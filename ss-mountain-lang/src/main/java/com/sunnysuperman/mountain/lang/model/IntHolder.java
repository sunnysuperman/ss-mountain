package com.sunnysuperman.mountain.lang.model;

public class IntHolder {
	int value;

	public IntHolder() {
		super();
	}

	public IntHolder(int value) {
		super();
		this.value = value;
	}

	public int get() {
		return value;
	}

	public void set(int value) {
		this.value = value;
	}

	public int add(int delta) {
		this.value = value + delta;
		return this.value;
	}

}
