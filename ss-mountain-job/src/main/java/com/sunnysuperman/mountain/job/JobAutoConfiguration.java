package com.sunnysuperman.mountain.job;

import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.sunnysuperman.mountain.job.executor.JobExecutor;
import com.sunnysuperman.mountain.lang.exception.Exceptions;

@Configuration(proxyBeanMethods = false)
public class JobAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.job.core")
	public JobCoreProperties jobCoreProperties() {
		return new JobCoreProperties();
	}

	@EventListener(ApplicationReadyEvent.class)
	@Order(Ordered.LOWEST_PRECEDENCE)
	public void onReady(ApplicationReadyEvent event) {
		// 添加到任务列表中
		ApplicationContext context = event.getApplicationContext();
		Map<String, JobExecutor> executorMap = context.getBeansOfType(JobExecutor.class);
		for (JobExecutor executor : executorMap.values()) {
			JobExecutorManager.add(executor);
		}
		// 检查任务是否为空
		JobCoreProperties props = context.getBean(JobCoreProperties.class);
		if (!props.isAllowEmpty() && JobExecutorManager.count() == 0) {
			throw Exceptions.wrapRuntimeException("No job executor found");
		}
		JobRegistrar jobRegistrar = context.getBean(JobRegistrar.class);
		JobExecutorManager.use(jobRegistrar);
	}

}
