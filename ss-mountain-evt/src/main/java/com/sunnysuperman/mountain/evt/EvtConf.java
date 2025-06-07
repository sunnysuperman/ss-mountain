package com.sunnysuperman.mountain.evt;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sunnysuperman.mountain.mq.MQConstants;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EvtConf {

	/** 事件名称，需要在服务内唯一 **/
	String name();

	/** 优先级，不同优先级执行策略有所不同，默认为中等优先级 **/
	EvtPriority priority() default EvtPriority.MEDIUM;

	/** 消费超时时间(秒)，默认为300秒(5分钟) **/
	int consumeTimeoutInSeconds() default 300;

	/** 是否需要持久化，默认需要 **/
	boolean persistent() default true;

	/** 是否禁用定时任务调度，默认启用任务 **/
	boolean jobDisabled() default false;

	/** 定时任务名称，默认为`${name}-event` **/
	String jobName() default "";

	/** 是否禁用消息列队收发事件，默认启用消息列队 **/
	boolean mqDisabled() default false;

	/** 消息队列名称，开启消息列队时必填 **/
	String mqName() default MQConstants.DEFAULT_NAME;

	/** 消息标签，默认为事件名称 **/
	String mqTag() default "";
}
