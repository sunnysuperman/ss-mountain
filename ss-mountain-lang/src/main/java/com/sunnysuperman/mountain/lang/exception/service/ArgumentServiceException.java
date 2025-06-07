package com.sunnysuperman.mountain.lang.exception.service;

import com.sunnysuperman.mountain.lang.utils.Colls;
import com.sunnysuperman.mountain.lang.utils.Str;

public class ArgumentServiceException extends ServiceException {
	private static final long serialVersionUID = -7398899188917026294L;

	public ArgumentServiceException(String key, Object value) {
		super(Colls.arrayAsMap("key", key, "value", value), GenericServiceError.ILLEGAL_ARGUMENT);
	}

	public ArgumentServiceException(String key) {
		this(key, null);
	}

	public static ArgumentServiceException wrapChild(ArgumentServiceException e, String key) {
		String childKey = e.getErrorData().get("key").toString();
		return new ArgumentServiceException(key + "." + childKey, e.getErrorData().get("value"));
	}

	public String getArgumentErrorKey() {
		return Str.parse(getErrorData().get("key"));
	}
}
