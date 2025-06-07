package com.sunnysuperman.mountain.lang.json;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.sunnysuperman.mountain.lang.enums.CodeAwareEnum;
import com.sunnysuperman.mountain.lang.enums.DescAwareEnum;
import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;

/** 带描述枚举反序列化器 **/
public final class DescAwareEnumDeserializer extends JsonDeserializer<DescAwareEnum> implements ContextualDeserializer {
	private Class<? extends DescAwareEnum> enumClass;

	public DescAwareEnumDeserializer() {
		super();
	}

	public DescAwareEnumDeserializer(Class<? extends DescAwareEnum> enumClass) {
		super();
		this.enumClass = enumClass;
	}

	@Override
	public DescAwareEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode node = p.getCodec().readTree(p);
		int code;
		if (node.canConvertToInt()) {
			code = node.asInt();
		} else if (node.isTextual()) {
			// 有时候客户端传过来的是字面量
			String literal = node.asText();
			if (Str.isEmpty(literal)) {
				return null;
			}
			if (!Str.isNumeric(literal)) {
				try {
					return (DescAwareEnum) (enumClass.getMethod("valueOf", String.class).invoke(null, literal));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					throw new UnexpectedException(e);
				}
			}
			code = Num.parseByte(literal);
		} else {
			JsonNode codeNode = node.get("code");
			if (codeNode == null) {
				return null;
			}
			code = codeNode.asInt();
		}
		return CodeAwareEnum.fromCode(enumClass, (byte) code);
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
			throws JsonMappingException {
		@SuppressWarnings("unchecked")
		Class<? extends DescAwareEnum> clazz = (Class<? extends DescAwareEnum>) ctxt.getContextualType().getRawClass();
		return new DescAwareEnumDeserializer(clazz);
	}

}
