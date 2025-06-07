package com.sunnysuperman.mountain.db.converter;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.utils.Str;

public class EmptyStringConverter implements FieldConverter<String> {

	@Override
	public Object convertToColumn(String fieldValue, SerializeContext context) {
		return fieldValue != null ? fieldValue : Str.EMPTY;
	}

	@Override
	public String convertToField(Object columnValue, Class<String> type, DeserializeContext context) {
		if (columnValue == null) {
			return null;
		}
		String s = columnValue.toString();
		if (s.isEmpty()) {
			return null;
		}
		return s;
	}

}
