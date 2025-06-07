package com.sunnysuperman.mountain.validation.validator;

import com.sunnysuperman.mountain.validation.ContextValidationsException;
import com.sunnysuperman.mountain.validation.ValidationContext;
import com.sunnysuperman.mountain.validation.Validator;
import com.sunnysuperman.validation.api.exception.ValidationsException;

public class EmptyToNullValidator implements Validator {

	@Override
	public void validate(Object obj, ValidationContext context) throws ValidationsException {
		if (obj == null) {
			return;
		}
		if (!(obj instanceof String)) {
			throw new ContextValidationsException(context, "not a string");
		}
		String s = obj.toString();
		if (s.isEmpty()) {
			context.updateFieldValue(null);
		}
	}

}
