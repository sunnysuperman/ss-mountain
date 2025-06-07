package com.sunnysuperman.mountain.lang.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sunnysuperman.mountain.lang.enums.CodeAwareEnum;

/** 枚举序列化器 **/
public class CodeAwareEnumSerializer extends JsonSerializer<CodeAwareEnum> {

	@Override
	public void serialize(CodeAwareEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeNumber(value.code());
	}

}
