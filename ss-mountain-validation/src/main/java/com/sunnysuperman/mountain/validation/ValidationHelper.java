package com.sunnysuperman.mountain.validation;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.sunnysuperman.mountain.lang.exception.service.ArgumentServiceException;
import com.sunnysuperman.mountain.lang.exception.service.DetailedServiceException;
import com.sunnysuperman.mountain.lang.exception.service.ServiceException;
import com.sunnysuperman.validation.api.exception.CustomMessageValidationsException;
import com.sunnysuperman.validation.api.exception.ValidationsException;

public class ValidationHelper implements BeanPostProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(ValidationHelper.class);

	public ValidationHelper(List<CustomValidator> validators) {
		if (validators == null) {
			return;
		}
		// 注册校验器
		validators.forEach(validator -> Validators.registerValidator(validator.annotationType(), validator));
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// 校验spring托管bean，一般为配置类
		if (bean.getClass().isAnnotationPresent(Valid.class)) {
			try {
				validate(bean, false);
			} catch (ValidationsException ex) {
				throw new ValidationsException(bean.getClass().getCanonicalName() + " 校验失败：" + ex.getMessage(), ex);
			}
		}
		return bean;
	}

	/** 手动校验对象，如果校验失败，包装成业务异常 **/
	public void validate(Object obj) {
		validate(obj, true);
	}

	/**
	 * 手动校验对象
	 * 
	 * @param obj              校验对象
	 * @param convertException 校验失败是否包装异常(如果不包装，返回校验器返回的异常)
	 */
	public void validate(Object obj, boolean convertException) {
		if (convertException) {
			doValidation(() -> Validators.validate(obj));
		} else {
			Validators.validate(obj);
		}
	}

	/** 校验方法参数，配合AOP，#ValidationAspect **/
	public void validateMethodParameter(String paramName, Object paramValue, Annotation[] paramAnnotations) {
		doValidation(() -> {
			// Spring-Controller参数拦截，如果是RequestBody，则不显示参数名
			boolean isRequestBody = false;
			for (Annotation annotation : paramAnnotations) {
				if (annotation.annotationType().getCanonicalName()
						.equals("org.springframework.web.bind.annotation.RequestBody")) {
					isRequestBody = true;
					break;
				}
			}
			Validators.validateMethodParameter(isRequestBody ? null : paramName, paramValue, paramAnnotations);
		});
	}

	private void doValidation(Runnable runnable) {
		try {
			runnable.run();
		} catch (CustomMessageValidationsException ex) {
			// 自定义校验错误消息，直接抛出
			throw new DetailedServiceException(ex.getMessage());
		} catch (ValidationsException | ValidationException ex) {
			// 非自定义校验错误消息（用户不友好），转成业务参数异常错误
			if (ex.getCause() != null) {
				LOG.error(ex.getMessage(), ex.getCause());
			}
			throw new ArgumentServiceException(ex.getMessage());
		} catch (ServiceException ex) {
			// 业务参数异常，直接抛出
			throw ex;
		}
	}

}
