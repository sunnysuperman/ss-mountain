package com.sunnysuperman.mountain.lang.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.sunnysuperman.mountain.lang.enums.CodeAwareEnum;

/** 枚举反序列化器 **/
public final class CodeAwareEnumDeserializer extends JsonDeserializer<CodeAwareEnum> implements ContextualDeserializer {
	private Class<? extends CodeAwareEnum> enumClass;

	public CodeAwareEnumDeserializer() {
		super();
	}

	public CodeAwareEnumDeserializer(Class<? extends CodeAwareEnum> enumClass) {
		super();
		this.enumClass = enumClass;
	}

	@Override
	public CodeAwareEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		JsonNode node = p.getCodec().readTree(p);
		int code;
		if (node.canConvertToInt()) {
			code = node.asInt();
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
		Class<? extends CodeAwareEnum> clazz = (Class<? extends CodeAwareEnum>) ctxt.getContextualType().getRawClass();
		return new CodeAwareEnumDeserializer(clazz);
	}

}
