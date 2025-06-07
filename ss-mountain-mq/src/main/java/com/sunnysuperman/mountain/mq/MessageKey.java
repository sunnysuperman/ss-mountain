package com.sunnysuperman.mountain.mq;

public enum MessageKey {

	MAC("__mac");

	private String key;

	private MessageKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

}
