package com.sunnysuperman.mountain.repository.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	String name() default "";

	boolean nullable() default true;

	boolean insertable() default true;

	boolean updatable() default true;

	int length() default 255;

	int precision() default 2;

	String comment() default "";

	String[] columnDefinition() default {};

	Class<?> converter() default void.class;

	boolean converterCache() default true;

}
