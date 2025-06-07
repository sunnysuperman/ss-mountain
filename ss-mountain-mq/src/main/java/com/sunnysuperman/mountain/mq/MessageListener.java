package com.sunnysuperman.mountain.mq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageListener {

	/** 列队名称 **/
	String queue() default MQConstants.DEFAULT_NAME;

	/** 消息标签 **/
	String tag();

}
