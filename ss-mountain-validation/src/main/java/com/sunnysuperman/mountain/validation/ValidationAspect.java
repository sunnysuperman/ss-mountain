package com.sunnysuperman.mountain.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

@Aspect
public class ValidationAspect {
	private static final Logger LOG = LoggerFactory.getLogger(ValidationAspect.class);
	private final ValidationHelper validationHelper;
	private final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	public ValidationAspect(ValidationHelper validationHelper) {
		super();
		this.validationHelper = validationHelper;
	}

	// 定义切点，拦截带有校验注解的方法
	@Pointcut("execution(* *(.., @javax.validation.Valid (*), ..)) || execution(* *(.., @javax.validation.constraints.NotNull (*), ..)) || execution(* *(.., @javax.validation.constraints.NotEmpty (*), ..))")
	public void validationAnnotation() {
		// nope
	}

	// 定义前置拦截，在目标方法执行之前调用
	@Before("validationAnnotation()")
	public void validate(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Annotation[][] paramsAnnotations = method.getParameterAnnotations();
		String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
		Object[] params = joinPoint.getArgs();
		for (int i = 0; i < params.length; i++) {
			Annotation[] paramAnnotations = paramsAnnotations[i];
			if (paramAnnotations != null && paramAnnotations.length > 0) {
				String paramName = paramNames == null ? null : paramNames[i];
				if (paramName == null) {
					LOG.error("No parameter name to validate: {}", method);
				}
				Object paramValue = params[i];
				validationHelper.validateMethodParameter(paramName, paramValue, paramAnnotations);
			}
		}
	}

}