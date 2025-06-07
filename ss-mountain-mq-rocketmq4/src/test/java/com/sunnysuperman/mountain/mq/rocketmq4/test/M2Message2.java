package com.sunnysuperman.mountain.mq.rocketmq4.test;

public class M2Message2 {
	public static final String TAG = "test789";

	String payload;

	public M2Message2() {
		super();
	}

	public M2Message2(String payload) {
		super();
		this.payload = payload;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

}