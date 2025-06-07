package com.sunnysuperman.mountain.db.converter;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.utils.Num;

public class ZeroIntegerConverter implements FieldConverter<Integer> {
	private static final Integer ZERO = 0;

	@Override
	public Object convertToColumn(Integer fieldValue, SerializeContext context) {
		return fieldValue != null ? fieldValue : ZERO;
	}

	@Override
	public Integer convertToField(Object columnValue, Class<Integer> type, DeserializeContext context) {
		if (columnValue == null) {
			return null;
		}
		Integer value = Num.parseInteger(columnValue);
		if (value.longValue() == 0) {
			return null;
		}
		return value;
	}

}
