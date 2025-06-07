package com.sunnysuperman.mountain.mq.rocketmq4.test;

public class DefaultMessage {
	public static final String TAG = "default123";

	String content;

	public DefaultMessage() {
		super();
	}

	public DefaultMessage(String content) {
		super();
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}