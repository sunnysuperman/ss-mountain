package com.sunnysuperman.mountain.localevent;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.sunnysuperman.mountain.task.TaskAutoConfiguration;
import com.sunnysuperman.mountain.task.TaskHelper;

@Configuration(proxyBeanMethods = false)
@Import({ TaskAutoConfiguration.class })
public class LocalEventAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.localevent")
	public LocalEventProperties localEventProperties() {
		return new LocalEventProperties();
	}

	@Bean
	public LocalEventSubscriberManager localEventSubscriberManager() {
		return new LocalEventSubscriberManager();
	}

	@Bean
	public LocalEventPublisher localEventPublisher(LocalEventProperties localEventProperties,
			LocalEventSubscriberManager localEventSubscriberManager, TaskHelper taskHelper) {
		return new LocalEventPublisher(localEventProperties, localEventSubscriberManager, taskHelper);
	}

}
