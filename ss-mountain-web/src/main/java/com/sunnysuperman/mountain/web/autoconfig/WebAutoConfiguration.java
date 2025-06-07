package com.sunnysuperman.mountain.web.autoconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({ WebValidationAutoConfiguration.class, WebApiAutoConfiguration.class, WebCrossDomainAutoConfiguration.class })
public class WebAutoConfiguration {

}
