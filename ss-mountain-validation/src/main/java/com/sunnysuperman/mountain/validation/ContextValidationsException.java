package com.sunnysuperman.mountain.validation;

import com.sunnysuperman.validation.api.exception.ValidationsException;

public class ContextValidationsException extends ValidationsException {
	private static final long serialVersionUID = -1L;

	private final transient ValidationContext context;

	public ContextValidationsException(ValidationContext context, String invalidMessage) {
		super(context.canonicalFieldName(), invalidMessage);
		this.context = context;
	}

	public ValidationContext getContext() {
		return context;
	}

}
