package com.sunnysuperman.mountain.mq.rocketmq4;

import com.sunnysuperman.mountain.lang.utils.Jsons;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.mq.MQConstants;

/** 消息通道 **/
public class RocketMQ4QueueProperties {
	// 名称
	private String name;
	// topic
	private String topic;
	// 标签前缀(一般用于隔离服务和开发环境)
	private String tagPrefix;
	// 详细日志
	private boolean verboseLog;

	public RocketMQ4QueueProperties() {
		super();
	}

	public RocketMQ4QueueProperties(String name, String topic, String tagPrefix) {
		super();
		this.name = name;
		this.topic = topic;
		this.tagPrefix = tagPrefix;
	}

	public void validate() {
		if (Str.isEmpty(name)) {
			name = MQConstants.DEFAULT_NAME;
		}
		if (Str.isEmpty(topic)) {
			throw new IllegalArgumentException("Require topic for queue: " + name);
		}
	}

	@Override
	public String toString() {
		return Jsons.write(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTagPrefix() {
		return tagPrefix;
	}

	public void setTagPrefix(String tagPrefix) {
		this.tagPrefix = tagPrefix;
	}

	public boolean isVerboseLog() {
		return verboseLog;
	}

	public void setVerboseLog(boolean verboseLog) {
		this.verboseLog = verboseLog;
	}

}
