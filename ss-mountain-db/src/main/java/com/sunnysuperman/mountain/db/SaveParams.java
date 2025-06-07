package com.sunnysuperman.mountain.db;

import java.util.List;

public class SaveParams {

	List<Object[]> params;

	List<Object> newVersions;

	public SaveParams(List<Object[]> params, List<Object> newVersions) {
		super();
		this.params = params;
		this.newVersions = newVersions;
	}

	public boolean versioning() {
		return newVersions != null && !newVersions.isEmpty();
	}

	public List<Object[]> getParams() {
		return params;
	}

	public List<Object> getNewVersions() {
		return newVersions;
	}

}
