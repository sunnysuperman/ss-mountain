package com.sunnysuperman.mountain.lang.pagination;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;

import com.sunnysuperman.mountain.lang.utils.Str;

public final class PullPageHelper {

	private PullPageHelper() {
	}

	public static <S, T> PullPage<T> listToPullPage(List<S> items, PullPageRequest pullPageRequest,
			Function<List<S>, List<T>> converter) {
		if (items == null || items.isEmpty()) {
			return PullPage.empty();
		}
		int startIndex = pullPageRequest.getMarker() == null ? 0 : Integer.parseInt(pullPageRequest.getMarker());
		if (items.size() <= startIndex) {
			return PullPage.empty();
		}
		int endIndex = Math.min(startIndex + pullPageRequest.getLimit(), items.size());
		List<S> subItems = items.subList(startIndex, endIndex);
		@SuppressWarnings("unchecked")
		List<T> convertedItems = converter != null ? converter.apply(subItems) : (List<T>) subItems;
		if (endIndex >= items.size()) {
			return PullPage.of(convertedItems, null, false);
		}
		return PullPage.of(convertedItems, String.valueOf(endIndex), true);
	}

	public static <T> PullPage<T> listToPullPage(List<T> items, PullPageRequest pullPageRequest) {
		return listToPullPage(items, pullPageRequest, null);
	}

	public static <K, T> PullPage<T> findForStateAwarePullPage(StateAwarePullPageFinder<K, T> finder,
			PullPageRequest request) {
		return findForStateAwarePullPage(finder, request.getMarker(), request.getLimit());
	}

	public static <K, T> PullPage<T> findForStateAwarePullPage(StateAwarePullPageFinder<K, T> finder, String marker,
			final int limit) {
		K state = null;
		String offset = null;
		if (Str.isNotEmpty(marker)) {
			try {
				marker = new String(Base64.getUrlDecoder().decode(marker), StandardCharsets.UTF_8);
				int tokenOffset = marker.indexOf('-');
				state = finder.parseState(marker.substring(0, tokenOffset));
				offset = marker.substring(tokenOffset + 1);
			} catch (Exception ex) {
				// ignore
			}
		} else {
			state = finder.getInitState();
		}
		if (state == null) {
			return PullPage.empty();
		}
		offset = Str.emptyToNull(offset);
		// 拉取数据
		PullPage<T> page = finder.findForPullPage(state, offset, limit);
		// 本状态下还有更多数据
		if (page.isHasMore()) {
			return PullPage.of(page.getContent(), encodeMarker(finder, state, page.getMarker()), true);
		}
		// 本状态下没有更多数据，把本状态下的数据先加入到结果集中，然后接着从下一个状态里查数据
		List<T> mergedItems = new ArrayList<>(Math.min(limit, 10));
		if (!page.getContent().isEmpty()) {
			mergedItems.addAll(page.getContent());
		}
		while (true) {
			K nextState = finder.getNextState(state);
			if (nextState == null) {
				return PullPage.of(mergedItems, null, false);
			}
			int nextLimit = limit - mergedItems.size();
			// 正好卡在最后一条数据，状态切换要交给下一次调用
			if (nextLimit <= 0) {
				// 校验是否还有数据
				boolean hasMore = !finder.findForPullPage(nextState, null, 1).getContent().isEmpty();
				return PullPage.of(mergedItems, encodeMarker(finder, nextState, Str.EMPTY), hasMore);
			}
			// 本次状态，拉取数据
			PullPage<T> page2 = finder.findForPullPage(nextState, null, nextLimit);
			mergedItems.addAll(page2.getContent());
			if (page2.isHasMore()) {
				return PullPage.of(mergedItems, encodeMarker(finder, nextState, page2.getMarker()), true);
			}
			state = nextState;
		}
	}

	private static <K> String encodeMarker(StateAwareFinder<K> finder, K state, String newMarker) {
		String marker = new StringBuilder(finder.serializeState(state)).append('-').append(newMarker).toString();
		return Base64.getUrlEncoder().encodeToString(marker.getBytes(StandardCharsets.UTF_8));
	}

}
