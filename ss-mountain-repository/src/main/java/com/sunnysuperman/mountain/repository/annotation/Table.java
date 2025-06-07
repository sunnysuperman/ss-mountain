package com.sunnysuperman.mountain.repository.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

	/**
	 * The name of the table.
	 */
	String name();

	/**
	 * The description of the table.
	 */
	String comment() default "";

	/**
	 * Convert column name to match 'UnderScoreCase' style
	 */
	boolean mapCamelToUnderscore() default true;

	/**
	 * (Optional) Indexes for the table. These are only used if table generation is
	 * in effect. Note that it is not necessary to specify an index for a primary
	 * key, as the primary key index will be created automatically.
	 */
	Index[] indexes() default {};
}
