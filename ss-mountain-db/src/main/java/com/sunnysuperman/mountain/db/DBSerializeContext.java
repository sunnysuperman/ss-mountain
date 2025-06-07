package com.sunnysuperman.mountain.db;

import java.util.Set;

public class DBSerializeContext implements SerializeContext {
	private Object entity;
	private InsertUpdate insertUpdate;
	private Set<String> fields;
	private DefaultFieldConverter defaultFieldConverter;

	public DBSerializeContext(Object entity, Set<String> fields, InsertUpdate insertUpdate,
			DefaultFieldConverter defaultFieldConverter) {
		super();
		this.entity = entity;
		this.fields = fields;
		this.insertUpdate = insertUpdate;
		this.defaultFieldConverter = defaultFieldConverter;
	}

	@Override
	public Object getEntity() {
		return entity;
	}

	@Override
	public Set<String> getFields() {
		return fields;
	}

	@Override
	public InsertUpdate getInsertUpdate() {
		return insertUpdate;
	}

	@Override
	public DefaultFieldConverter getDefaultFieldConverter() {
		return defaultFieldConverter;
	}

}
