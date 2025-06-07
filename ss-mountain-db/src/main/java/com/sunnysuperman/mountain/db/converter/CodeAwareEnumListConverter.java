package com.sunnysuperman.mountain.db.converter;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.enums.CodeAwareEnum;
import com.sunnysuperman.mountain.lang.utils.Jsons;

public class CodeAwareEnumListConverter<T extends CodeAwareEnum> implements FieldConverter<List<T>> {
	// prevent from sonar warning in 'convertToField' method
	private List<T> emptyList = null;

	@Override
	public Object convertToColumn(List<T> fieldValue, SerializeContext context) {
		if (fieldValue == null) {
			return null;
		}
		return Jsons.write(fieldValue.stream().map(CodeAwareEnum::code).collect(Collectors.toList()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> convertToField(Object columnValue, Class<List<T>> type, DeserializeContext context) {
		if (columnValue == null) {
			return emptyList;
		}
		Class<T> enumClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		return Jsons.readForList(columnValue.toString(), Byte.class).stream()
				.map(i -> CodeAwareEnum.fromCode(enumClass, i)).collect(Collectors.toList());
	}

}
