package com.sunnysuperman.mountain.mq;

import java.util.Date;
import java.util.Properties;

/**
 * 消息发送选项
 */
public class MQProduceOptions {
	// 消息标签
	private String tag;
	// 预约发送时间
	private Date deliveryTime;
	// 消息头
	private Properties headers;
	// 是否异步
	private Boolean sync;
	// 最大尝试发送次数(<=1表示不重试)
	private int maxAttempts;

	public String getTag() {
		return tag;
	}

	public MQProduceOptions setTag(String tag) {
		this.tag = tag;
		return this;
	}

	public Date getDeliveryTime() {
		return deliveryTime;
	}

	public MQProduceOptions setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
		return this;
	}

	public Properties getHeaders() {
		return headers;
	}

	public MQProduceOptions setHeaders(Properties headers) {
		this.headers = headers;
		return this;
	}

	public Boolean getSync() {
		return sync;
	}

	public MQProduceOptions setSync(Boolean sync) {
		this.sync = sync;
		return this;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public MQProduceOptions setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
		return this;
	}

}
