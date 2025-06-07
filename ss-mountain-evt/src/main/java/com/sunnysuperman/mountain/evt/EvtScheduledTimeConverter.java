package com.sunnysuperman.mountain.evt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.MultiColumn;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.id.ObjectIdGeneratorFactory;
import com.sunnysuperman.mountain.lang.id.ObjectIdGeneratorFactory.ObjectIdGenerator;
import com.sunnysuperman.mountain.lang.pagination.MarkerUtils;
import com.sunnysuperman.mountain.lang.utils.Num;

public class EvtScheduledTimeConverter implements FieldConverter<Date> {
	private static final ObjectIdGenerator ID_GENERATOR = ObjectIdGeneratorFactory.create();

	@Override
	public Object convertToColumn(Date fieldValue, SerializeContext context) {
		Map<String, Object> columns = new HashMap<>();
		columns.put("scheduled_time", fieldValue.getTime());
		columns.put("_scheduled_time", MarkerUtils.getDateMarker(fieldValue, ID_GENERATOR.generate()));
		return new MultiColumn(columns);
	}

	@Override
	public Date convertToField(Object columnValue, Class<Date> type, DeserializeContext context) {
		return new Date(Num.parseLong(columnValue));
	}

}
