package com.sunnysuperman.mountain.db.converter;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.utils.Bool;

public class BooleanConverter implements FieldConverter<Boolean> {

	@Override
	public Object convertToColumn(Boolean fieldValue, SerializeContext context) {
		if (fieldValue == null) {
			return null;
		}
		return fieldValue ? 1 : 0;
	}

	@Override
	public Boolean convertToField(Object columnValue, Class<Boolean> type, DeserializeContext context) {
		return columnValue == null ? null : Bool.parse(columnValue);
	}

}
