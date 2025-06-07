package com.sunnysuperman.mountain.file;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration(proxyBeanMethods = false)
public class FileAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.file")
	public FileProperties fileProperties() {
		return new FileProperties();
	}

	@Bean
	public FileHelper fileHelper(FileProperties fileProperties, Environment environment) {
		return new FileHelper(fileProperties, environment);
	}

	@Bean
	public FileDownloader fileDownloader(FileHelper fileHelper) {
		return new FileDownloader(fileHelper);
	}

}
