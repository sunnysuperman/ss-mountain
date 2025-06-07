package com.sunnysuperman.mountain.validation.validator;

import java.math.BigDecimal;

import com.sunnysuperman.mountain.validation.ContextValidationsException;
import com.sunnysuperman.mountain.validation.ValidationContext;
import com.sunnysuperman.mountain.validation.Validator;
import com.sunnysuperman.validation.api.exception.ValidationsException;

public class PositiveOrZeroValidator implements Validator {

	@Override
	public void validate(Object obj, ValidationContext context) throws ValidationsException {
		BigDecimal decimal = parseBigDecimal(obj, context);
		if (decimal == null) {
			return;
		}
		if (decimal.compareTo(BigDecimal.ZERO) < 0) {
			throw context.wrapValidationException(this::wrapValidationException);
		}
	}

	protected ValidationsException wrapValidationException(ValidationContext context) {
		return new ContextValidationsException(context, "should >=0");
	}

	private static BigDecimal parseBigDecimal(Object obj, ValidationContext context) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof BigDecimal) {
			return (BigDecimal) obj;
		}
		if (obj instanceof Number) {
			return new BigDecimal(obj.toString());
		}
		throw new ContextValidationsException(context, "not a number");
	}

}
