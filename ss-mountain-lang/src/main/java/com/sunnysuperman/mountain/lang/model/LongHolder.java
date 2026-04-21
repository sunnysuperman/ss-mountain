package com.sunnysuperman.mountain.lang.model;

public class LongHolder {
	long value;

	public LongHolder() {
		super();
	}

	public LongHolder(long value) {
		super();
		this.value = value;
	}

	public long get() {
		return value;
	}

	public void set(long value) {
		this.value = value;
	}

	public long add(long delta) {
		this.value = value + delta;
		return this.value;
	}

	public long add() {
		return add(1);
	}

}
