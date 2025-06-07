package com.sunnysuperman.mountain.mq;

public interface MQConsumer {

	void start() throws MQException;

	void shutdown();

}
