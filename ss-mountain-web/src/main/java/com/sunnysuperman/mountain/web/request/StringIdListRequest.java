package com.sunnysuperman.mountain.web.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Valid
public class StringIdListRequest {

	@NotEmpty
	private List<String> ids;

	public StringIdListRequest() {
		super();
	}

	public StringIdListRequest(@NotNull List<String> ids) {
		super();
		this.ids = ids;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

}
