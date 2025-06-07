package com.sunnysuperman.mountain.lang.pagination;

public class PageRequest {
	protected int pageNo = 1;
	protected int limit = Page.DEFAULT_PAGESIZE;

	public PageRequest() {
		super();
	}

	public PageRequest(int pageNo, int limit) {
		super();
		setPageNo(pageNo);
		setLimit(limit);
	}

	public int getOffset() {
		return (pageNo - 1) * limit;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		if (pageNo <= 0) {
			throw new IllegalArgumentException("pageNo");
		}
		this.pageNo = pageNo;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("limit");
		}
		this.limit = limit;
	}

	public static PageRequest of(int pageNo, int limit) {
		return new PageRequest(pageNo, limit);
	}
}
