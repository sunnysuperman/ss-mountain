package com.sunnysuperman.mountain.web;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.sunnysuperman.mountain.base.BaseProperties;
import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.base.context.Context;
import com.sunnysuperman.mountain.base.context.ContextThreadLocal;
import com.sunnysuperman.mountain.base.locale.ErrorLocaleBundle;
import com.sunnysuperman.mountain.lang.exception.service.ArgumentServiceException;
import com.sunnysuperman.mountain.lang.exception.service.RuntimeServiceException;
import com.sunnysuperman.mountain.lang.exception.service.ServiceException;
import com.sunnysuperman.mountain.lang.exception.service.ServiceExceptions;
import com.sunnysuperman.mountain.lang.exception.service.ServiceLogger;
import com.sunnysuperman.mountain.lang.model.ApiResult;
import com.sunnysuperman.mountain.lang.utils.Obj;
import com.sunnysuperman.mountain.web.annotation.ResponseContentType;
import com.sunnysuperman.mountain.web.view.HttpStatusView;
import com.sunnysuperman.mountain.web.view.JsonView;
import com.sunnysuperman.mountain.web.view.NoneView;

public class DefaultExceptionResolver implements HandlerExceptionResolver {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultExceptionResolver.class);
	private static final ServiceLogger SERVICE_LOG = new ServiceLogger(LOG);
	private static final RuntimeServiceException DEFAULT_EXCEPTION = new RuntimeServiceException();
	@Resource
	private EnvHelper envHelper;
	@Resource
	private BaseProperties baseProperties;

	@PostConstruct
	public void init() {
		// 校验字串配置
		if (envHelper.isLocal() && envHelper.isStrictMode() && baseProperties.getScanPackages() != null) {
			ErrorLocaleBundle.validate(baseProperties.getScanPackages());
		}
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// 1.处理非业务异常
		ModelAndView resp = resolveNonServiceException(ex, handler);
		if (resp != null) {
			return resp;
		}
		// 2.处理业务异常
		return resolveServiceException(ex, handler);
	}

	/** 处理非业务异常 **/
	protected ModelAndView resolveNonServiceException(Exception ex, Object handler) {
		if (ex instanceof NoHandlerFoundException) {
			// 404
			return new ModelAndView(new HttpStatusView(HttpStatus.NOT_FOUND));
		} else if (ex instanceof HttpRequestMethodNotSupportedException) {
			// 405
			return new ModelAndView(new HttpStatusView(HttpStatus.METHOD_NOT_ALLOWED));
		} else if (ex instanceof ServletException) {
			// 400
			LOG.error(null, ex);
			return new ModelAndView(new HttpStatusView(HttpStatus.BAD_REQUEST));
		} else {
			// 处理未进入业务方法之前的业务参数问题
			Context context = ContextThreadLocal.getInstance().get();
			// 如果没有请求上下文，说明未进入默认拦截器，一般就是业务参数问题了
			if (ex instanceof HttpMessageConversionException || context == null) {
				LOG.error(null, ex);
				return resolveServiceException(new ArgumentServiceException("please check request parameters"),
						handler);
			} else {
				// 交给后续异常处理器
				return null;
			}
		}
	}

	/** 处理业务异常 **/
	protected ModelAndView resolveServiceException(Exception ex, Object handler) {
		// 记录错误日志
		SERVICE_LOG.logExceptServiceException(ex);
		// 提取业务异常
		ServiceException serviceException = Obj.or(ServiceExceptions.extract(ex, 3), DEFAULT_EXCEPTION);
		// 输出业务异常结果
		ResponseContentType responseType = !(handler instanceof HandlerMethod) ? null
				: ((HandlerMethod) handler).getMethodAnnotation(ResponseContentType.class);
		ContentType contentType = responseType == null ? ContentType.JSON : responseType.value();
		if (contentType == ContentType.JSON) {
			return new ModelAndView(new JsonView(exceptionToResult(serviceException)));
		} else {
			// 非JSON格式返回空，后续有需要再扩展
			return new ModelAndView(new NoneView());
		}
	}

	protected Object exceptionToResult(ServiceException se) {
		ApiResult<String> result = new ApiResult<>();
		result.setErrCode(se.getErrorCode());
		result.setErrMsg(ErrorLocaleBundle.getErrorMsg(se));
		result.setErrData(se.getErrorData());
		return result;
	}

}
