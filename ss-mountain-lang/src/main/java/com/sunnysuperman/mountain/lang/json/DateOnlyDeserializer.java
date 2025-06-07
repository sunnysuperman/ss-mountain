package com.sunnysuperman.mountain.lang.json;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.sunnysuperman.mountain.lang.utils.Dates;
import com.sunnysuperman.mountain.lang.utils.Str;

public class DateOnlyDeserializer extends JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		String s = p.getValueAsString();
		if (Str.isEmpty(s)) {
			return null;
		}
		return Dates.parseDateOnly(s);
	}

}
