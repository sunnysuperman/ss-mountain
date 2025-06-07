package com.sunnysuperman.mountain.mq.rocketmq5;

import javax.annotation.PostConstruct;

public class RocketMQ5ProducerProperties extends RocketMQ5Properties {
	// 是否懒加载(生产环境建议关闭)
	private boolean lazyInit;
	// 是否同步发送
	private boolean sync;
	// 发送超时时间
	private int sendMsgTimeoutMillis;
	// 最大尝试发送次数
	private int maxAttempts = 2;

	@PostConstruct
	public void init() {
		validate();
	}

	@Override
	public void validate() {
		super.validate();

		if (sendMsgTimeoutMillis <= 0) {
			sendMsgTimeoutMillis = 3000;
		}
	}

	public boolean isLazyInit() {
		return lazyInit;
	}

	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public int getSendMsgTimeoutMillis() {
		return sendMsgTimeoutMillis;
	}

	public void setSendMsgTimeoutMillis(int sendMsgTimeoutMillis) {
		this.sendMsgTimeoutMillis = sendMsgTimeoutMillis;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

}
