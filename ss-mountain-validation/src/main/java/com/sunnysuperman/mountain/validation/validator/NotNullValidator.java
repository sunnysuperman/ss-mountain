package com.sunnysuperman.mountain.validation.validator;

import com.sunnysuperman.mountain.validation.ContextValidationsException;
import com.sunnysuperman.mountain.validation.ValidationContext;
import com.sunnysuperman.mountain.validation.Validator;
import com.sunnysuperman.validation.api.exception.ValidationsException;

public class NotNullValidator implements Validator {

	@Override
	public void validate(Object obj, ValidationContext context) throws ValidationsException {
		if (obj == null) {
			throw context.wrapValidationException(this::wrapValidationException);
		}
	}

	protected ValidationsException wrapValidationException(ValidationContext context) {
		return new ContextValidationsException(context, "should not be null");
	}

}
