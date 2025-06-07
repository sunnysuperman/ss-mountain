package com.sunnysuperman.mountain.validation.validator;

import com.sunnysuperman.mountain.validation.ContextValidationsException;
import com.sunnysuperman.mountain.validation.ValidationContext;
import com.sunnysuperman.mountain.validation.Validator;
import com.sunnysuperman.validation.api.Length;
import com.sunnysuperman.validation.api.exception.ValidationsException;

public class LengthValidator implements Validator {

	@Override
	public void validate(Object obj, ValidationContext context) throws ValidationsException {
		if (obj == null) {
			obj = "";
		}
		if (!(obj instanceof String)) {
			throw new ContextValidationsException(context, "not a string");
		}
		String s = obj.toString();
		int len = s.length();
		Length meta = (Length) context.getAnnotation();
		if (len < meta.min()) {
			throw context.wrapValidationException(this::wrapValidationExceptionByMin);
		}
		if (len > meta.max()) {
			throw context.wrapValidationException(this::wrapValidationExceptionByMax);
		}
	}

	protected ValidationsException wrapValidationExceptionByMin(ValidationContext context) {
		Length meta = (Length) context.getAnnotation();
		return new ContextValidationsException(context, "length should >=" + meta.min());
	}

	protected ValidationsException wrapValidationExceptionByMax(ValidationContext context) {
		Length meta = (Length) context.getAnnotation();
		return new ContextValidationsException(context, "length should <=" + meta.max());
	}

}
