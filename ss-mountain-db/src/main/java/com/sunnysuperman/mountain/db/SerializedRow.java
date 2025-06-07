package com.sunnysuperman.mountain.db;

import java.util.Map;

public class SerializedRow {
	private String tableName;
	private Map<String, Object> data;
	private Map<String, Object> upsertData;
	private String[] idColumns;
	private Object[] idValues;
	private boolean idGeneration;
	// Versioning for optimistic locking
	private boolean versioning;
	private Object updatedVersion;
	private Object insertedVersion;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Map<String, Object> getUpsertData() {
		return upsertData;
	}

	public void setUpsertData(Map<String, Object> upsertData) {
		this.upsertData = upsertData;
	}

	public String[] getIdColumns() {
		return idColumns;
	}

	public void setIdColumns(String[] idColumns) {
		this.idColumns = idColumns;
	}

	public Object[] getIdValues() {
		return idValues;
	}

	public void setIdValues(Object[] idValues) {
		this.idValues = idValues;
	}

	public boolean isIdGeneration() {
		return idGeneration;
	}

	public void setIdGeneration(boolean idGeneration) {
		this.idGeneration = idGeneration;
	}

	public boolean isVersioning() {
		return versioning;
	}

	public void setVersioning(boolean versioning) {
		this.versioning = versioning;
	}

	public Object getUpdatedVersion() {
		return updatedVersion;
	}

	public void setUpdatedVersion(Object updatedVersion) {
		this.updatedVersion = updatedVersion;
	}

	public Object getInsertedVersion() {
		return insertedVersion;
	}

	public void setInsertedVersion(Object insertedVersion) {
		this.insertedVersion = insertedVersion;
	}

}
