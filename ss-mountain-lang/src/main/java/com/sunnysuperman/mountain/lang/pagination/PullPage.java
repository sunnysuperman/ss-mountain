package com.sunnysuperman.mountain.lang.pagination;

import java.util.Collections;
import java.util.List;

import com.sunnysuperman.mountain.lang.exception.UnexpectedException;
import com.sunnysuperman.mountain.lang.utils.Str;

public class PullPage<T> {
	protected List<T> content;
	protected String marker;
	protected boolean hasMore;

	public static <T> PullPage<T> of(List<T> content, String marker, boolean hasMore) {
		if (content == null) {
			content = Collections.emptyList();
		}
		if (hasMore) {
			if (Str.isEmpty(marker)) {
				throw new UnexpectedException("marker should not be null if has more");
			}
		} else {
			marker = null;
		}
		PullPage<T> pr = new PullPage<>();
		pr.content = content;
		pr.marker = marker;
		pr.hasMore = hasMore;
		return pr;
	}

	public static <T> PullPage<T> of(List<T> content) {
		return of(content, null, false);
	}

	public static <T> PullPage<T> empty() {
		return of(null, null, false);
	}

	public static <T> PullPage<T> singleton(T data) {
		return of(Collections.singletonList(data), null, false);
	}

	public static <T> PullPage<T> extend(PullPage<?> page, List<T> data) {
		return of(data, page.getMarker(), page.hasMore);
	}

	public boolean hasContent() {
		return content != null && !content.isEmpty();
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

}
