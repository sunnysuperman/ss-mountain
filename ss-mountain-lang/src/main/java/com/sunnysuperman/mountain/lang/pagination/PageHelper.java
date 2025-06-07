package com.sunnysuperman.mountain.lang.pagination;

import java.util.List;
import java.util.function.Function;

import com.sunnysuperman.mountain.lang.utils.Colls;

public final class PageHelper {

	private PageHelper() {
	}

	public static <S, T> Page<T> listToPage(List<S> items, PageRequest pageRequest,
			Function<List<S>, List<T>> converter) {
		int total = items.size();
		if (total == 0) {
			return Page.empty(pageRequest);
		}
		int offset = pageRequest.getOffset();
		int limit = pageRequest.getLimit();
		List<S> subItems = Colls.subListByOffsetAndSize(items, offset, limit);
		@SuppressWarnings("unchecked")
		List<T> convertedItems = converter != null ? converter.apply(subItems) : (List<T>) subItems;
		return Page.of(convertedItems, total, offset, limit);
	}

	public static <T> Page<T> listToPage(List<T> items, PageRequest pageRequest) {
		return listToPage(items, pageRequest, null);
	}

}
