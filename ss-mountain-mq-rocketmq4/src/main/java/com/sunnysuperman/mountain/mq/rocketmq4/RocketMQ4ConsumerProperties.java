package com.sunnysuperman.mountain.mq.rocketmq4;

import javax.annotation.PostConstruct;

import com.sunnysuperman.mountain.lang.utils.Str;

public class RocketMQ4ConsumerProperties extends RocketMQ4Properties {
	// 消费者分组名
	private String group;
	// 是否广播模式
	private boolean broadcasting;
	// 消息消费失败时的最大重试次数。
	private int maxReconsumeTimes = 20;
	// 消息消费失败进行重试前的等待时间，单位（毫秒），取值范围: 10毫秒~30,000毫秒。
	private int suspendTimeMillis = 1000;
	// 消息消费的最大超时时间，超过这个时间，这条消息将被视为消费失败，等待重新投递. 单位(分钟)
	private int consumeTimeout = 15;

	@PostConstruct
	public void init() {
		validate();
	}

	@Override
	public void validate() {
		super.validate();

		if (Str.isEmpty(group)) {
			throw new IllegalArgumentException("group is empty");
		}
		if (maxReconsumeTimes <= 0) {
			throw new IllegalArgumentException("maxReconsumeTimes");
		}
		if (suspendTimeMillis < 10 || suspendTimeMillis > 30000) {
			throw new IllegalArgumentException("suspendTimeMillis");
		}
		if (consumeTimeout <= 0) {
			throw new IllegalArgumentException("consumeTimeout");
		}
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isBroadcasting() {
		return broadcasting;
	}

	public void setBroadcasting(boolean broadcasting) {
		this.broadcasting = broadcasting;
	}

	public int getMaxReconsumeTimes() {
		return maxReconsumeTimes;
	}

	public void setMaxReconsumeTimes(int maxReconsumeTimes) {
		this.maxReconsumeTimes = maxReconsumeTimes;
	}

	public int getSuspendTimeMillis() {
		return suspendTimeMillis;
	}

	public void setSuspendTimeMillis(int suspendTimeMillis) {
		this.suspendTimeMillis = suspendTimeMillis;
	}

	public int getConsumeTimeout() {
		return consumeTimeout;
	}

	public void setConsumeTimeout(int consumeTimeout) {
		this.consumeTimeout = consumeTimeout;
	}

}
