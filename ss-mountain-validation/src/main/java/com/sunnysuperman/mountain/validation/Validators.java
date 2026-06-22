package com.sunnysuperman.mountain.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunnysuperman.mountain.validation.validator.EmptyToNullValidator;
import com.sunnysuperman.mountain.validation.validator.LengthValidator;
import com.sunnysuperman.mountain.validation.validator.MaxValidator;
import com.sunnysuperman.mountain.validation.validator.MinValidator;
import com.sunnysuperman.mountain.validation.validator.MobileNumberValidator;
import com.sunnysuperman.mountain.validation.validator.NegativeOrZeroValidator;
import com.sunnysuperman.mountain.validation.validator.NegativeValidator;
import com.sunnysuperman.mountain.validation.validator.NotEmptyValidator;
import com.sunnysuperman.mountain.validation.validator.NotNullValidator;
import com.sunnysuperman.mountain.validation.validator.NullToEmptyValidator;
import com.sunnysuperman.mountain.validation.validator.NullValidator;
import com.sunnysuperman.mountain.validation.validator.PositiveOrZeroValidator;
import com.sunnysuperman.mountain.validation.validator.PositiveValidator;
import com.sunnysuperman.mountain.validation.validator.SetNullValidator;
import com.sunnysuperman.validation.api.EmptyToNull;
import com.sunnysuperman.validation.api.Length;
import com.sunnysuperman.validation.api.MobileNumber;
import com.sunnysuperman.validation.api.NullToEmpty;
import com.sunnysuperman.validation.api.SetNull;
import com.sunnysuperman.validation.api.Validated;
import com.sunnysuperman.validation.api.exception.ValidationsException;

public class Validators {
	private static final Logger LOG = LoggerFactory.getLogger(Validators.class);
	// 类校验字段缓存
	private static Map<Class<?>, ValidationField[]> validationFieldsMap = new ConcurrentHashMap<>();
	// 内置校验类
	private static Map<Class<? extends Annotation>, Validator> validatorMap = new ConcurrentHashMap<>();

	static {
		validatorMap.put(NotNull.class, new NotNullValidator());
		validatorMap.put(NotEmpty.class, new NotEmptyValidator());
		validatorMap.put(Null.class, new NullValidator());
		validatorMap.put(SetNull.class, new SetNullValidator());
		validatorMap.put(NullToEmpty.class, new NullToEmptyValidator());
		validatorMap.put(EmptyToNull.class, new EmptyToNullValidator());
		validatorMap.put(Length.class, new LengthValidator());
		validatorMap.put(Min.class, new MinValidator());
		validatorMap.put(Max.class, new MaxValidator());
		validatorMap.put(Positive.class, new PositiveValidator());
		validatorMap.put(PositiveOrZero.class, new PositiveOrZeroValidator());
		validatorMap.put(Negative.class, new NegativeValidator());
		validatorMap.put(NegativeOrZero.class, new NegativeOrZeroValidator());
		validatorMap.put(MobileNumber.class, new MobileNumberValidator());
	}

	private Validators() {
	}

	public static void registerValidator(Class<? extends Annotation> annotationType, Validator validator) {
		validatorMap.put(annotationType, validator);
		if (!validationFieldsMap.isEmpty()) {
			LOG.warn("[Validators] register validator {} after validation started!!!", validator);
			validationFieldsMap.clear();
		}
	}

	public static void validate(Object obj) throws ValidationsException {
		if (obj == null) {
			return;
		}
		ValidationContext context = new ValidationContext();
		doValidate(obj, context);
	}

	public static void validateMethodParameter(String paramName, Object paramValue, Annotation[] paramAnnotations)
			throws ValidationsException {
		ValidationContext context = new ValidationContext();
		for (Annotation annotation : paramAnnotations) {
			context.setAnnotation(annotation);
			Class<? extends Annotation> annotaiontType = annotation.annotationType();
			if (paramName != null) {
				context.pushField(paramName);
			}
			if (annotaiontType == Valid.class) {
				doValidate(paramValue, context);
			} else {
				Validator validator = validatorMap.get(annotaiontType);
				if (validator != null) {
					validator.validate(paramValue, context);
				}
			}
			if (paramName != null) {
				context.popField();
			}
		}
	}

	@SuppressWarnings("all")
	private static void doValidate(Object obj, ValidationContext context) throws ValidationsException {
		if (obj == null) {
			return;
		}
		// 如果字段值是集合或数组，需要嵌套校验
		if (obj instanceof Collection) {
			doValidateCollection(obj, context);
			return;
		}
		Class<?> type = obj.getClass();
		if (type.isArray()) {
			doValidateArray(obj, context);
			return;
		}
		// 对象校验
		boolean doValidated = false;
		if (type.isAnnotationPresent(Valid.class)) {
			ValidationField[] validationFields = validationFieldsMap.computeIfAbsent(type,
					Validators::findValidationFields);
			// 校验各个字段
			for (ValidationField f : validationFields) {
				context.pushField(f.field.getName());
				try {
					Field field = f.field;
					Object fieldValue;
					try {
						field.setAccessible(true);
						fieldValue = field.get(obj);
					} catch (IllegalAccessException e) {
						throw new ValidationsException("failed to get field value: " + context.canonicalFieldName(), e);
					}
					// 1个字段会有多种校验
					for (Annotation annotation : f.annotations) {
						context.setAnnotation(annotation);
						Validator validator = validatorMap.get(annotation.annotationType());
						validator.validate(fieldValue, context);
						if (context.isFieldValueUpdated()) {
							fieldValue = context.getUpdatedFieldValue();
						}
					}
					// 如果字段值更新了，需要设置回对象
					if (context.isFieldValueUpdated()) {
						try {
							field.set(obj, fieldValue);
						} catch (IllegalArgumentException | IllegalAccessException e) {
							throw new ValidationsException("failed to set field value: " + context.canonicalFieldName(),
									e);
						}
					}
					// 嵌套校验
					if (fieldValue != null && f.cascading) {
						doValidate(fieldValue, context);
					}
				} finally {
					context.popField();
				}
			}
		}
		// 自定义校验
		if (obj instanceof Validated) {
			Validated validated = (Validated) obj;
			validated.validate();
		}
	}

	private static void doValidateCollection(Object obj, ValidationContext context) throws ValidationsException {
		@SuppressWarnings("rawtypes")
		Collection collection = (Collection) obj;
		for (Object element : collection) {
			doValidate(element, context);
		}
	}

	private static void doValidateArray(Object obj, ValidationContext context) throws ValidationsException {
		int arrayLength = Array.getLength(obj);
		for (int i = 0; i < arrayLength; i++) {
			Object element = Array.get(obj, i);
			doValidate(element, context);
		}
	}

	/** 获取某个类的校验字段 **/
	private static ValidationField[] findValidationFields(Class<?> type) {
		List<ValidationField> validationFields = new ArrayList<>();
		while (type != null) {
			for (Field field : type.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					// 字段校验条件：
					// 1.字段配置了校验注解
					// 2.字段为校验对象(Valid/Validated)
					List<Annotation> annotations = Stream.of(field.getAnnotations())
							.filter(anno -> validatorMap.get(anno.annotationType()) != null)
							.collect(Collectors.toList());
					boolean toValidate = !annotations.isEmpty();
					boolean cascading = field.isAnnotationPresent(Valid.class) || shouldTypeToValidate(field.getType());
					if (toValidate || cascading) {
						validationFields.add(new ValidationField(field, annotations, cascading));
					}
				}
			}
			type = type.getSuperclass();
		}
		return validationFields.toArray(new ValidationField[validationFields.size()]);
	}

	private static boolean shouldTypeToValidate(Class<?> type) {
		return type.isAnnotationPresent(Valid.class) || Validated.class.isAssignableFrom(type);
	}

	private static class ValidationField {
		Field field;
		List<Annotation> annotations;
		boolean cascading;

		public ValidationField(Field field, List<Annotation> annotations, boolean cascading) {
			super();
			this.field = field;
			this.annotations = annotations;
			this.cascading = cascading;
		}

	}

}
