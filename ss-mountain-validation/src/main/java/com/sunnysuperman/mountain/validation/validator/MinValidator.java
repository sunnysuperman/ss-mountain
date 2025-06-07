package com.sunnysuperman.mountain.validation.validator;

import java.math.BigDecimal;

import javax.validation.constraints.Min;

import com.sunnysuperman.mountain.validation.ContextValidationsException;
import com.sunnysuperman.mountain.validation.ValidationContext;
import com.sunnysuperman.mountain.validation.Validator;
import com.sunnysuperman.validation.api.exception.ValidationsException;

public class MinValidator implements Validator {

	@Override
	public void validate(Object obj, ValidationContext context) throws ValidationsException {
		Double val = parseDouble(obj, context);
		if (val == null) {
			return;
		}
		double doubleValue = val.doubleValue();
		Min meta = (Min) context.getAnnotation();
		if (doubleValue < meta.value()) {
			throw context.wrapValidationException(this::wrapValidationException);
		}
	}

	protected ValidationsException wrapValidationException(ValidationContext context) {
		Min meta = (Min) context.getAnnotation();
		return new ContextValidationsException(context, "should >=" + meta.value());
	}

	private static Double parseDouble(Object obj, ValidationContext context) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Double) {
			return (Double) obj;
		}
		if (obj instanceof Number) {
			return new BigDecimal(obj.toString()).doubleValue();
		}
		throw new ContextValidationsException(context, "not a number");
	}

}
