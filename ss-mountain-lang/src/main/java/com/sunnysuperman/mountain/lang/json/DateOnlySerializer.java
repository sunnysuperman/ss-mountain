package com.sunnysuperman.mountain.lang.json;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sunnysuperman.mountain.lang.utils.Dates;

public class DateOnlySerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeNull();
			return;
		}
		gen.writeString(Dates.formatDateOnly(value));
	}

}
