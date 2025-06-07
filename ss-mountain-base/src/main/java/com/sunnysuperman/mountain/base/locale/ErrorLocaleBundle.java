package com.sunnysuperman.mountain.base.locale;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.exception.service.RuntimeServiceException;
import com.sunnysuperman.mountain.lang.exception.service.ServiceErrorCode;
import com.sunnysuperman.mountain.lang.exception.service.ServiceException;
import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.lang.utils.Types;

public class ErrorLocaleBundle {
	private static final Logger LOG = LoggerFactory.getLogger(ErrorLocaleBundle.class);
	private static final RuntimeServiceException DEFAULT_EXCEPTION = new RuntimeServiceException();

	private ErrorLocaleBundle() {
	}

	public static void validate(String[] packages) {
		if (LOG.isInfoEnabled()) {
			LOG.info(">>>>>>[locale] error locales initialized with packages: {}", Str.join(packages));
		}
		Set<Class<?>> types = Types.findSubTypesOf(packages, ServiceErrorCode.class);
		for (Class<?> type : types) {
			@SuppressWarnings("unchecked")
			Class<ServiceErrorCode> errType = (Class<ServiceErrorCode>) type;
			ServiceErrorCode[] codes = errType.getEnumConstants();
			for (ServiceErrorCode code : codes) {
				String key = "err." + code.code();
				if (!LocaleBundles.containsKey(key)) {
					throw new UnexpectedException("No string configured: " + key);
				}
			}
		}
	}

	public static boolean isGenericError(int errorCode) {
		return errorCode < 100;
	}

	public static String getErrorMsg(ServiceException se, String locale) {
		if (se.getErrorMsg() != null) {
			return se.getErrorMsg();
		}
		return LocaleBundles.getWithArrayParams(locale, "err." + se.getErrorCode(), se.getErrorParams());
	}

	public static String getErrorMsg(Throwable e, String locale) {
		ServiceException se;
		if (e instanceof ServiceException) {
			se = (ServiceException) e;
		} else {
			se = DEFAULT_EXCEPTION;
		}
		return getErrorMsg(se, locale);
	}

	public static String getErrorMsg(Throwable e) {
		return getErrorMsg(e, null);
	}
}
