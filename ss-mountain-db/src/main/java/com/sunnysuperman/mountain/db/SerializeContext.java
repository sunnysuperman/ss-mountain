package com.sunnysuperman.mountain.db;

import java.util.Set;

public interface SerializeContext {

	Object getEntity();

	Set<String> getFields();

	InsertUpdate getInsertUpdate();

	DefaultFieldConverter getDefaultFieldConverter();

}
