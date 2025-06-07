package com.sunnysuperman.mountain.lang.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sunnysuperman.mountain.lang.enums.DescAwareEnum;

/** 带描述枚举序列化器 **/
public class DescAwareEnumSerializer extends JsonSerializer<DescAwareEnum> {

	@Override
	public void serialize(DescAwareEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeNumberField("code", value.code());
		gen.writeStringField("desc", value.description());
		gen.writeEndObject();
	}

}
