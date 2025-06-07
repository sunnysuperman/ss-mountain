package com.sunnysuperman.mountain.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sunnysuperman.validation.api.exception.CustomMessageValidationsException;
import com.sunnysuperman.validation.api.exception.ValidationsException;

public class ValidationContext {

	private static Map<Class<?>, Method> messageMethodMap = new ConcurrentHashMap<>();

	private Annotation annotation;

	private LinkedList<String> fields = new LinkedList<>();

	private boolean fieldValueUpdated;
	private Object updatedFieldValue;

	public Annotation getAnnotation() {
		return annotation;
	}

	void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public void pushField(String fieldName) {
		fields.addLast(fieldName);
	}

	public void popField() {
		fields.removeLast();
		fieldValueUpdated = false;
	}

	public String fieldName() {
		return fields.getLast();
	}

	public String canonicalFieldName() {
		if (fields.size() == 1) {
			return fields.get(0);
		}
		return fields.stream().collect(Collectors.joining("."));
	}

	public void updateFieldValue(Object fieldValue) {
		this.updatedFieldValue = fieldValue;
		this.fieldValueUpdated = true;
	}

	public Object getUpdatedFieldValue() {
		return updatedFieldValue;
	}

	public boolean isFieldValueUpdated() {
		return fieldValueUpdated;
	}

	public ValidationsException wrapValidationException(
			Function<ValidationContext, ValidationsException> messageProvider) {
		String message;
		try {
			message = (String) getMessageMethod().invoke(annotation);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ValidationsException("failed to call message() of " + annotation, e);
		}
		// 如果用户配置了错误提示，则直接使用该错误提示
		if (!message.isEmpty() && message.charAt(0) != '{') {
			return new CustomMessageValidationsException(message);
		}
		return messageProvider.apply(this);
	}

	private Method getMessageMethod() {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		Method method = messageMethodMap.get(annotationType);
		if (method == null) {
			try {
				method = annotationType.getMethod("message");
			} catch (NoSuchMethodException | SecurityException e) {
				throw new ValidationsException("require message() of " + annotation, e);
			}
			messageMethodMap.put(annotationType, method);
		}
		return method;
	}

}
