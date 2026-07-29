package com.sunnysuperman.mountain.web.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Valid
public class IntegerIdListRequest {

	@NotEmpty
	private List<Integer> ids;

	public IntegerIdListRequest() {
		super();
	}

	public IntegerIdListRequest(@NotNull List<Integer> ids) {
		super();
		this.ids = ids;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

}
