package com.sunnysuperman.mountain.swagger2;

import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sunnysuperman.mountain.base.BaseProperties;
import com.sunnysuperman.mountain.lang.utils.Obj;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.web.WebUtils;

import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration(proxyBeanMethods = false)
public class SwaggerAutoConfiguration {
	private static final Logger LOG = LoggerFactory.getLogger(SwaggerAutoConfiguration.class);

	@Bean
	@ConfigurationProperties("ss-mountain.swagger2")
	public SwaggerProperties swaggerProperties() {
		return new SwaggerProperties();
	}

	@Bean
	public Docket docket(SwaggerProperties swaggerProperties, BaseProperties baseProperties) {
		// 未开启文档功能，由于引入了文档库，会自动开启文档，需要在这里关闭
		if (swaggerProperties == null || !swaggerProperties.isEnabled()) {
			return new Docket(DocumentationType.SWAGGER_2).enable(false);
		}
		Predicate<RequestHandler> packageFilter = null;
		String[] packages = Obj.or(swaggerProperties.getScanPackages(), baseProperties.getScanPackages());
		if (LOG.isInfoEnabled()) {
			LOG.info(">>>>>>[swagger2] init: {}", Str.join(packages));
		}
		for (String pkg : packages) {
			if (packageFilter == null) {
				packageFilter = RequestHandlerSelectors.basePackage(pkg);
			} else {
				packageFilter = packageFilter.or(RequestHandlerSelectors.basePackage(pkg));
			}
		}
		SecurityContext securityContext = SecurityContext.builder()
				.securityReferences(List.of(SecurityReference.builder().scopes(new AuthorizationScope[0])
						.reference(WebUtils.KEY_AUTHORIZATION).build()))
				.operationSelector(o -> o.requestMappingPattern().matches("/.*")).build();
		return new Docket(DocumentationType.SWAGGER_2)
				.securitySchemes(List.of(new ApiKey(WebUtils.KEY_AUTHORIZATION, WebUtils.KEY_AUTHORIZATION, "header")))
				.securityContexts(List.of(securityContext)).select().apis(packageFilter).paths(PathSelectors.any())
				.build().apiInfo(new ApiInfoBuilder().title("API").license(null).build());
	}

}
