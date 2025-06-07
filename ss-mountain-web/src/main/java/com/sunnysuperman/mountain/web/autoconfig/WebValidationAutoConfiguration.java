package com.sunnysuperman.mountain.web.autoconfig;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunnysuperman.mountain.base.BaseProperties;
import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.lang.utils.IOUtil;
import com.sunnysuperman.mountain.lang.utils.ProcessUtil;
import com.sunnysuperman.mountain.lang.utils.Types;
import com.sunnysuperman.mountain.web.annotation.ControllerValidationIgnored;

@Configuration(proxyBeanMethods = false)
public class WebValidationAutoConfiguration {
	private static final Logger LOG = LoggerFactory.getLogger(WebValidationAutoConfiguration.class);

	@Resource
	private BaseProperties baseProperties;
	@Resource
	private EnvHelper envHelper;

	@PostConstruct
	private void init() throws IOException {
		validateUploadDir();
	}

	@EventListener(ApplicationReadyEvent.class)
	@Order(Ordered.LOWEST_PRECEDENCE)
	private void onReady() {
		validateController();
	}

	/** 校验上传目录 **/
	private void validateUploadDir() throws IOException {
		String multipartLocation = envHelper.getEnv().getProperty("spring.servlet.multipart.location");
		if (multipartLocation == null || multipartLocation.isEmpty() || multipartLocation.startsWith("/tmp")) {
			throw new IllegalArgumentException("上传目录配置不正确");
		}
		File dir = new File(multipartLocation);
		// 先清除上传目录
		IOUtil.deleteFile(dir);
		// 再重新生成上传目录
		dir.mkdirs();
		if (!dir.exists()) {
			throw new IllegalArgumentException("无法创建上传目录: " + dir.getAbsolutePath());
		}
	}

	/** 校验控制器 **/
	private void validateController() {
		if (!envHelper.isLocal()) {
			// 非本地开发模式不校验
			return;
		}
		// 为了加速启动速度，应用启动后N秒后再执行校验逻辑
		Timer timer = new Timer(true);
		timer.schedule(new ControllerValidator(), 5000);
	}

	private class ControllerValidator extends TimerTask {
		StringBuilder errors = new StringBuilder();

		@Override
		public void run() {
			String[] packages = baseProperties.getScanPackages();
			Set<Class<?>> types = Types.findTypesAnnotatedWith(packages, Controller.class, RestController.class);
			for (Class<?> type : types) {
				doValidateController(type);
			}
			if (errors.length() > 0) {
				errors.insert(0, "==============ERROR==============" + IOUtil.LINE);
				if (envHelper.isStrictMode()) {
					ProcessUtil.exitWithMessage(errors.toString(), null);
				} else if (LOG.isErrorEnabled()) {
					LOG.error(errors.toString());
				}
			}
			LOG.info(">>>>>>[web] controllers validated");
		}

		private void doValidateController(Class<?> type) {
			if (type.isAnnotationPresent(ControllerValidationIgnored.class)) {
				return;
			}
			Arrays.stream(type.getDeclaredMethods()).forEach(method -> {
				if (!Modifier.isPublic(method.getModifiers())) {
					return;
				}
				// 忽略校验
				if (method.isAnnotationPresent(ControllerValidationIgnored.class)) {
					return;
				}
				// 校验请求映射
				validateRequestMapping(method, type);
				// 校验请求参数
				validateRequestParams(method, type);
			});
		}

		private void appendError(String s) {
			errors.append(s).append(IOUtil.LINE);
		}

		private void validateRequestParams(Method method, Class<?> type) {
			Parameter[] params = method.getParameters();
			if (params == null || params.length == 0) {
				return;
			}
			Arrays.stream(params).forEach(param -> {
				if (!param.isAnnotationPresent(RequestBody.class)) {
					return;
				}
				Class<?> paramType = param.getType();
				// body如果是原始数据(String或字节数组)，不校验@Valid
				if (paramType == String.class || Types.isByteArray(paramType)) {
					return;
				}
				if (!param.isAnnotationPresent(Valid.class)) {
					appendError("Consider to use @Valid for " + type.getCanonicalName() + "/" + method.getName());
					return;
				}
				if (!isTypeAnnotatedWithValid(paramType, param.getParameterizedType())) {
					appendError("Consider to use @Valid for " + param.getType().getCanonicalName() + ", used by "
							+ type.getCanonicalName() + "/" + method.getName());
				}
			});
		}

		private boolean isTypeAnnotatedWithValid(Class<?> type, Type parameterizedType) {
			Class<?> realType;
			if (type == List.class || type == Set.class) {
				if (parameterizedType == null) {
					return false;
				}
				ParameterizedType ptype = (ParameterizedType) parameterizedType;
				Type itemType = ptype.getActualTypeArguments().length > 0 ? ptype.getActualTypeArguments()[0] : null;
				if (!(itemType instanceof Class)) {
					return false;
				}
				if (itemType == String.class || itemType == Integer.class || itemType == Long.class) {
					return true;
				}
				realType = (Class<?>) itemType;
			} else {
				realType = type;
			}
			return realType.isAnnotationPresent(Valid.class);
		}

		private void validateRequestMapping(Method method, Class<?> type) {
			Object mapping = getRequestMapping(method);
			if (mapping == null) {
				appendError("No RequestMapping for " + type.getCanonicalName() + "/" + method.getName());
				return;
			}
			if (!isValidMapping(method, mapping)) {
				appendError("Bad RequestMapping path for " + type.getCanonicalName() + "/" + method.getName());
			}
		}

		private Object getRequestMapping(Method method) {
			Object postMapping = method.getAnnotation(PostMapping.class);
			if (postMapping != null) {
				return postMapping;
			}
			Object getMapping = method.getAnnotation(GetMapping.class);
			if (getMapping != null) {
				return getMapping;
			}
			return method.getAnnotation(RequestMapping.class);
		}

		private boolean isValidMapping(Method method, Object mapping) {
			String[] mappingValues;
			try {
				mappingValues = (String[]) mapping.getClass().getMethod("value").invoke(mapping);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				LOG.error(null, e);
				return false;
			}
			String mappingValue = (mappingValues == null || mappingValues.length == 0) ? null : mappingValues[0];
			if (mappingValue == null || !mappingValue.startsWith("/")) {
				return false;
			}
			// "/submodule/action"
			mappingValue = mappingValue.substring(1);
			// "/get/{id}"
			int placeholderIndex = mappingValue.indexOf('{');
			if (placeholderIndex >= 0) {
				mappingValue = mappingValue.substring(0, placeholderIndex - 1);
			}
			int lastPathIndex = mappingValue.lastIndexOf("/");
			if (lastPathIndex >= 0) {
				mappingValue = mappingValue.substring(lastPathIndex + 1);
			}
			return mappingValue.equals(method.getName());
		}

	}

}
