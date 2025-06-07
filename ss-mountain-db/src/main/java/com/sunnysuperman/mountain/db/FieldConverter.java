package com.sunnysuperman.mountain.db;

public interface FieldConverter<T> {

	Object convertToColumn(T fieldValue, SerializeContext context);

	T convertToField(Object columnValue, Class<T> type, DeserializeContext context);
}
