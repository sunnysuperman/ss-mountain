package com.sunnysuperman.mountain.mq.rocketmq4.test;

public class MMMessage {
	public static final String TAG = "mm123";

	String mmText;

	public MMMessage() {
		super();
	}

	public MMMessage(String mmText) {
		super();
		this.mmText = mmText;
	}

	public String getMmText() {
		return mmText;
	}

	public void setMmText(String mmText) {
		this.mmText = mmText;
	}

}