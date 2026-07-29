package com.sunnysuperman.mountain.web.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
public class IntegerIdRequest {

	@NotNull
	private Integer id;

	public IntegerIdRequest() {
		super();
	}

	public IntegerIdRequest(Integer id) {
		super();
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
