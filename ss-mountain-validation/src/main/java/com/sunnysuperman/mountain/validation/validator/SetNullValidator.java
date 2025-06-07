package com.sunnysuperman.mountain.validation.validator;

import com.sunnysuperman.mountain.validation.ValidationContext;
import com.sunnysuperman.mountain.validation.Validator;
import com.sunnysuperman.validation.api.exception.ValidationsException;

public class SetNullValidator implements Validator {

	@Override
	public void validate(Object obj, ValidationContext context) throws ValidationsException {
		if (obj == null) {
			return;
		}
		context.updateFieldValue(null);
	}

}
