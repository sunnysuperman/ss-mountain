package com.sunnysuperman.mountain.db.converter;

import java.util.Date;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.id.ObjectIdGenerator;
import com.sunnysuperman.mountain.lang.id.ObjectIdGeneratorFactory;
import com.sunnysuperman.mountain.lang.pagination.MarkerUtils;
import com.sunnysuperman.mountain.lang.utils.Str;

public class DateMarkerConverter implements FieldConverter<Date> {

	private static class Constants {
		private static final ObjectIdGenerator ID_GENERATOR = ObjectIdGeneratorFactory.create();
		private static final String NIL = "0";
	}

	@Override
	public Object convertToColumn(Date fieldValue, SerializeContext context) {
		if (fieldValue == null) {
			return Constants.NIL;
		}
		return MarkerUtils.getDateMarker(fieldValue, Constants.ID_GENERATOR.generate());
	}

	@Override
	public Date convertToField(Object columnValue, Class<Date> type, DeserializeContext context) {
		if (columnValue == null) {
			return null;
		}
		String s = columnValue.toString();
		if (Str.isEmpty(s) || s.equals(Constants.NIL)) {
			return null;
		}
		return MarkerUtils.parseDateFromMarker(s);
	}

}
