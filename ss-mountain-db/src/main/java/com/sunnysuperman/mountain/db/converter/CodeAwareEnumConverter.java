package com.sunnysuperman.mountain.db.converter;

import com.sunnysuperman.mountain.db.DeserializeContext;
import com.sunnysuperman.mountain.db.FieldConverter;
import com.sunnysuperman.mountain.db.SerializeContext;
import com.sunnysuperman.mountain.lang.enums.CodeAwareEnum;
import com.sunnysuperman.mountain.lang.utils.Num;

public class CodeAwareEnumConverter implements FieldConverter<CodeAwareEnum> {

	@Override
	public Object convertToColumn(CodeAwareEnum fieldValue, SerializeContext context) {
		if (fieldValue == null) {
			return null;
		}
		return fieldValue.code();
	}

	@Override
	public CodeAwareEnum convertToField(Object columnValue, Class<CodeAwareEnum> type, DeserializeContext context) {
		if (columnValue == null) {
			return null;
		}
		return CodeAwareEnum.fromCode(type, Num.parseByte(columnValue));
	}

}
