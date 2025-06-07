package com.sunnysuperman.mountain.validation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ValidationAutoConfiguration {

	@Bean
	public ValidationHelper validationHelper(@Autowired(required = false) List<CustomValidator> validators) {
		return new ValidationHelper(validators);
	}

	@Bean
	public ValidationAspect validationAspect(ValidationHelper validationHelper) {
		return new ValidationAspect(validationHelper);
	}

}
