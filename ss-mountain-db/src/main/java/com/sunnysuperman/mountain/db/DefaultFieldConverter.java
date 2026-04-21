package com.sunnysuperman.mountain.db;

import java.lang.reflect.Field;

public interface DefaultFieldConverter {

	Object convertToColumn(Object fieldValue);

	Object convertToField(Object columnValue, Field field);
}
