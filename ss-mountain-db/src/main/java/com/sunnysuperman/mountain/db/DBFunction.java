package com.sunnysuperman.mountain.db;

import java.util.List;

public class DBFunction {
	private String function;
	private List<Object> params;

	public DBFunction(String function, List<Object> params) {
		super();
		this.function = function;
		this.params = params;
	}

	public String getFunction() {
		return function;
	}

	public List<Object> getParams() {
		return params;
	}

}
