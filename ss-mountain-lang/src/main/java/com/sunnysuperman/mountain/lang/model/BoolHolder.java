package com.sunnysuperman.mountain.lang.model;

public final class BoolHolder {

	private boolean value;

	public BoolHolder() {
		super();
	}

	public BoolHolder(boolean value) {
		super();
		this.value = value;
	}

	public boolean get() {
		return value;
	}

	public void setTrue() {
		this.value = true;
	}

	public void setFalse() {
		this.value = false;
	}

	public void set(boolean value) {
		this.value = value;
	}

}
