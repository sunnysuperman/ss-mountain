package com.sunnysuperman.mountain.repository.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Index {

	/**
	 * (Optional) The name of the index; defaults to a provider-generated name.
	 */
	String name() default "";

	/**
	 * (Required) The names of the columns to be included in the index, in order.
	 */
	String[] columns();

	/**
	 * (Optional) Whether the index is unique.
	 */
	boolean unique() default false;
}
