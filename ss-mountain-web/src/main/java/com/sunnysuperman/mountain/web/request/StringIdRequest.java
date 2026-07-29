package com.sunnysuperman.mountain.web.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
public class StringIdRequest {

	@NotNull
	private String id;

	public StringIdRequest() {
		super();
	}

	public StringIdRequest(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
