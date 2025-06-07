package com.sunnysuperman.mountain.job.xxl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sunnysuperman.mountain.job.xxl.XxlJobProperties.JobExecutorConfig;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

@Configuration(proxyBeanMethods = false)
public class XxlJobAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.job.xxl")
	public XxlJobProperties jobProperties() {
		return new XxlJobProperties();
	}

	@Bean
	public XxlJobSpringExecutor xxlJobExecutor(XxlJobProperties props, ApplicationContext context) {
		JobExecutorConfig exeConfig = props.getExecutor() != null ? props.getExecutor() : new JobExecutorConfig();

		XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
		executor.setAdminAddresses(props.getAdminAddresses());
		executor.setAccessToken(props.getAccessToken());
		executor.setAppname(exeConfig.getAppname() != null ? exeConfig.getAppname()
				: context.getEnvironment().getProperty("spring.application.name"));
		executor.setAddress(exeConfig.getAddress());
		executor.setIp(exeConfig.getIp());
		executor.setPort(exeConfig.getPort());
		executor.setLogPath(exeConfig.getLogPath());
		executor.setLogRetentionDays(exeConfig.getLogRetentionDays());

		return executor;
	}

	@Bean
	public XxlJobRegistrar xxlJobRegistrar() {
		return new XxlJobRegistrar();
	}

}
