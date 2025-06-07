package com.sunnysuperman.mountain.mq.rocketmq4.test;

public class M2Message {
	public static final String TAG = "test456";

	String text;

	public M2Message() {
		super();
	}

	public M2Message(String text) {
		super();
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}