package com.sunnysuperman.mountain.db.mapper;

import java.util.Map;

import com.sunnysuperman.mountain.lang.utils.Jsons;

public class BeanMapper<T> implements DBMapper<T> {
	private Class<T> type;

	public BeanMapper(Class<T> type) {
		super();
		this.type = type;
	}

	@Override
	public T map(Map<String, Object> row) {
		String s = Jsons.write(CamelizedMapDBMapper.getInstance().map(row));
		return Jsons.read(s, type);
	}

}
