package com.sunnysuperman.mountain.db.converter;

import java.util.Date;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.utils.Num;

public class NullDateConverter implements FieldConverter<Date> {
	private static final Long NIL = -1L;

	@Override
	public Object convertToColumn(Date fieldValue, SerializeContext context) {
		return fieldValue == null ? NIL : fieldValue.getTime();
	}

	@Override
	public Date convertToField(Object columnValue, Class<Date> type, DeserializeContext context) {
		if (columnValue == null) {
			return null;
		}
		Long timestamp = Num.parseLong(columnValue);
		if (timestamp < 0) {
			return null;
		}
		return new Date(timestamp);
	}

}
