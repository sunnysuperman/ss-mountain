package com.sunnysuperman.mountain.db.mapper;

import java.util.Map;

public interface DBMapper<T> {

	T map(Map<String, Object> row);

}
