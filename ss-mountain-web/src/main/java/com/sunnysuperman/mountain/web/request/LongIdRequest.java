package com.sunnysuperman.mountain.web.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
public class LongIdRequest {

	@NotNull
	private Long id;

	public LongIdRequest() {
		super();
	}

	public LongIdRequest(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
