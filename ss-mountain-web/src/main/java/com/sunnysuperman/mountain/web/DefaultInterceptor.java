package com.sunnysuperman.mountain.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.base.context.Context;
import com.sunnysuperman.mountain.base.context.ContextThreadLocal;
import com.sunnysuperman.mountain.web.annotation.DevMode;
import com.sunnysuperman.mountain.web.crossdomain.CrossDomainProperties;

public class DefaultInterceptor implements HandlerInterceptor {
	@Resource
	private EnvHelper envHelper;
	@Resource
	private CrossDomainProperties crossDomainProperties;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 跨域处理
		if (crossDomainProperties.isEnabled() && CrossDomainHandler.handle(request, response)) {
			return false;
		}
		if (!(handler instanceof HandlerMethod)) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		// 除了本地环境，屏蔽测试接口
		if (handlerMethod.getMethodAnnotation(DevMode.class) != null && !envHelper.isLocal()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return false;
		}
		// 上下文
		Context context = newContext();
		context.setRequestIp(WebUtils.getRemoteAddress(request));
		ContextThreadLocal.getInstance().set(context);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// nope
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// 清除上下文
		ContextThreadLocal.getInstance().set(null);
	}

	/** 注册拦截器 **/
	public InterceptorRegistration register(InterceptorRegistry registry) {
		return registry.addInterceptor(this).order(Ordered.HIGHEST_PRECEDENCE).addPathPatterns("/**")
				.excludePathPatterns("/swagger-ui/**");
	}

	/** 新建上下文 **/
	protected Context newContext() {
		return new Context();
	}

}
