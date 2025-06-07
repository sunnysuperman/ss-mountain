package com.sunnysuperman.mountain.validation;

import java.lang.annotation.Annotation;

public interface CustomValidator extends Validator {

	Class<? extends Annotation> annotationType();

}
