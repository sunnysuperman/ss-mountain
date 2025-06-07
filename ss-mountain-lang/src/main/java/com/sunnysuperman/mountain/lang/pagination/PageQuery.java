package com.sunnysuperman.mountain.lang.pagination;

import javax.validation.Valid;
import javax.validation.ValidationException;

import com.sunnysuperman.validation.api.Validated;

/** 分页查询 **/
@Valid
public class PageQuery implements Validated {
	private PageRequest page;

	@Override
	public final void validate() {
		if (page == null) {
			if (!allowPageRequestNull()) {
				throw new ValidationException("page");
			}
		} else {
			if (page.getLimit() > maxLimit()) {
				throw new ValidationException("page.limit");
			}
		}
		validateOthers();
	}

	public final void forPage(int pageNo, int limit) {
		page = new PageRequest(pageNo, limit);
	}

	public final void forPage(int limit) {
		forPage(1, limit);
	}

	public final <T> Page<T> forEmptyPageContent() {
		return Page.empty(page);
	}

	public PageRequest getPage() {
		return page;
	}

	public void setPage(PageRequest page) {
		this.page = page;
	}

	/** 其他校验, 继承类实现 **/
	protected void validateOthers() {
		// nope
	}

	/** 分页最大条数 **/
	protected int maxLimit() {
		return 100;
	}

	/** 是否允许分页参数为空 **/
	protected boolean allowPageRequestNull() {
		return false;
	}

}
