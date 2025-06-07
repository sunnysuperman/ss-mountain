package com.sunnysuperman.mountain.db;

import java.lang.reflect.Type;

public interface DefaultFieldConverter {

	Object convertToColumn(Object fieldValue);

	Object convertToField(Object columnValue, Class<?> type, Type genericType);
}
