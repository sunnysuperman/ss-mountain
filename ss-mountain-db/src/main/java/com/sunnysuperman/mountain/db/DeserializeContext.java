package com.sunnysuperman.mountain.db;

import java.util.Map;

public interface DeserializeContext {

	Map<String, Object> getColumns();

	Object getColumn(String name);

	DefaultFieldConverter getDefaultFieldConverter();

}
