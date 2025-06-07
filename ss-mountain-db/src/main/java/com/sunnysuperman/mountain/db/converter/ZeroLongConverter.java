package com.sunnysuperman.mountain.db.converter;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.utils.Num;

public class ZeroLongConverter implements FieldConverter<Long> {
	private static final Long ZERO = 0L;

	@Override
	public Object convertToColumn(Long fieldValue, SerializeContext context) {
		return fieldValue != null ? fieldValue : ZERO;
	}

	@Override
	public Long convertToField(Object columnValue, Class<Long> type, DeserializeContext context) {
		if (columnValue == null) {
			return null;
		}
		Long value = Num.parseLong(columnValue);
		if (value.longValue() == 0) {
			return null;
		}
		return value;
	}

}
