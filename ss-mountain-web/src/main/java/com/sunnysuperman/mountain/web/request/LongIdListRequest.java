package com.sunnysuperman.mountain.web.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Valid
public class LongIdListRequest {

	@NotEmpty
	private List<Long> ids;

	public LongIdListRequest() {
		super();
	}

	public LongIdListRequest(@NotNull List<Long> ids) {
		super();
		this.ids = ids;
	}

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

}
