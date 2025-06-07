package com.sunnysuperman.mountain.db.converter;

import java.util.List;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.utils.Str;

public class StringListConverter implements FieldConverter<List<String>> {
	private static final List<String> EMPTY = null;

	@Override
	public Object convertToColumn(List<String> fieldValue, SerializeContext context) {
		return fieldValue != null ? Str.join(fieldValue) : null;
	}

	@Override
	public List<String> convertToField(Object columnValue, Class<List<String>> type, DeserializeContext context) {
		if (columnValue == null) {
			return EMPTY;
		}
		return Str.split(columnValue.toString());
	}

}
