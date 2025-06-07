package com.sunnysuperman.mountain.task;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TaskAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.task.generic")
	public GenericTaskProperties genericTaskProperties() {
		return new GenericTaskProperties();
	}

	@Bean
	@ConfigurationProperties("ss-mountain.task.scheduled")
	public ScheduledTaskProperties scheduledTaskProperties() {
		return new ScheduledTaskProperties();
	}

	@Bean
	@ConfigurationProperties("ss-mountain.task.parallel")
	public ParallelTaskProperties parallelTaskProperties() {
		return new ParallelTaskProperties();
	}

	@Bean
	public TaskHelper taskHelper(GenericTaskProperties genericTaskProperties,
			ScheduledTaskProperties scheduledTaskProperties, ParallelTaskProperties parallelTaskProperties) {
		return new TaskHelper(genericTaskProperties, scheduledTaskProperties, parallelTaskProperties);
	}

}
