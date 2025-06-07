package com.sunnysuperman.mountain.lang.pagination;

import javax.validation.Valid;
import javax.validation.ValidationException;

import com.sunnysuperman.mountain.lang.utils.Str;
import com.sunnysuperman.validation.api.Validated;

/** 下拉分页查询 **/
@Valid
public class PullPageQuery implements Validated {
	private PullPageRequest pullPage;

	@Override
	public final void validate() {
		if (pullPage == null) {
			if (!allowPullPageRequestNull()) {
				throw new ValidationException("pullPage");
			}
		} else {
			if (pullPage.getLimit() > maxLimit()) {
				throw new ValidationException("pullPage.limit");
			}
		}
		validateOthers();
	}

	public final void forPullPage(String marker, int limit) {
		pullPage = new PullPageRequest(Str.emptyToNull(marker), limit);
	}

	public final void forPullPage(int limit) {
		forPullPage(null, limit);
	}

	public final <T> PullPage<T> forEmptyPullPageContent() {
		return PullPage.empty();
	}

	public PullPageRequest getPullPage() {
		return pullPage;
	}

	public void setPullPage(PullPageRequest pullPage) {
		this.pullPage = pullPage;
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
	protected boolean allowPullPageRequestNull() {
		return false;
	}

}
