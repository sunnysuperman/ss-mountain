package com.sunnysuperman.mountain.lang.pagination;

import com.sunnysuperman.mountain.lang.exception.service.ArgumentServiceException;

public class PageQueryWrap {
	private PageRequest page;
	private PullPageRequest pullPage;

	public PageRequest getPage() {
		return page;
	}

	public void setPage(PageRequest page) {
		this.page = page;
	}

	public PullPageRequest getPullPage() {
		return pullPage;
	}

	public void setPullPage(PullPageRequest pullPage) {
		this.pullPage = pullPage;
	}

	public void validateForPage() {
		if (page == null) {
			throw new ArgumentServiceException("page");
		}
	}

	public void forPage(int pageNo, int limit) {
		setPage(PageRequest.of(pageNo, limit));
	}

	public void forPage(int limit) {
		forPage(1, limit);
	}

	public <T> Page<T> resultForEmptyPage() {
		return Page.empty(page);
	}

	public void forPullPage(String marker, int limit) {
		setPullPage(PullPageRequest.of(marker, limit));
	}

	public void forPullPage(int limit) {
		forPullPage(null, limit);
	}

	public void validateForPullPage() {
		if (pullPage == null) {
			throw new ArgumentServiceException("pullPage");
		}
	}

	public <T> PullPage<T> resultForEmptyPullPage() {
		return PullPage.empty();
	}

}
