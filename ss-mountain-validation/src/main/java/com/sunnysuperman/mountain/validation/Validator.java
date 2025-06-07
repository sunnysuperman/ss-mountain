package com.sunnysuperman.mountain.validation;

import com.sunnysuperman.validation.api.exception.ValidationsException;

public interface Validator {

	void validate(Object obj, ValidationContext context) throws ValidationsException;

}
