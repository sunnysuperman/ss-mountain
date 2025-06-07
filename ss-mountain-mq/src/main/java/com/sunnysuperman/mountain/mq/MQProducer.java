package com.sunnysuperman.mountain.mq;

public interface MQProducer {

	/**
	 * 发送消息
	 * 
	 * @param queueName 消息通道名称
	 * @param msg       消息内容(POJO/二进制/String等)
	 * @param options   消息选项
	 */
	void produce(String queueName, Object msg, MQProduceOptions options) throws MQException;

	/** 发送消息 **/
	default void produce(Object msg, MQProduceOptions options) throws MQException {
		produce(MQConstants.DEFAULT_NAME, msg, options);
	}

	/** 停止生产者 **/
	void shutdown();

}
