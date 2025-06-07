package com.sunnysuperman.mountain.validation.validator;

import org.springframework.stereotype.Component;

import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.mountain.validation.ValidationContext;
import com.sunnysuperman.mountain.validation.Validator;
import com.sunnysuperman.validation.api.MobileNumber;
import com.sunnysuperman.validation.api.exception.ValidationsException;

@Component
public class MobileNumberValidator implements Validator {

	@Override
	public void validate(Object obj, ValidationContext context) throws ValidationsException {
		MobileNumber meta = (MobileNumber) context.getAnnotation();
		String s = Str.parse(obj);
		if (Str.isEmpty(s)) {
			if (meta.allowEmpty()) {
				context.updateFieldValue(null);
				return;
			}
			throw context.wrapValidationException(this::wrapValidationException);
		}
		String phoneNo = MobileNumberUtils.format(s);
		if (phoneNo == null) {
			throw context.wrapValidationException(this::wrapValidationException);
		}
		context.updateFieldValue(phoneNo);
	}

	protected ValidationsException wrapValidationException(ValidationContext context) {
		return new ValidationsException(context.canonicalFieldName(), "not a valid phone number");
	}

}
