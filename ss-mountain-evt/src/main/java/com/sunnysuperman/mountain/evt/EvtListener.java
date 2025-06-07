package com.sunnysuperman.mountain.evt;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EvtListener {

	/**
	 * 监听事件类
	 * 
	 * 一般不需要配置，以监听事件的方法的第一个参数为准
	 **/
	Class<? extends Evt>[] value() default {};

	/**
	 * 监听执行优先级
	 * 
	 * 数值小的优先执行，即最高优先级为Integer.MIN_VALUE，最低优先级为Integer.MAX_VALUE，默认为最低优先级
	 * 相同优先级的，不保证执行先后顺序
	 **/
	int order() default Integer.MAX_VALUE;

}
