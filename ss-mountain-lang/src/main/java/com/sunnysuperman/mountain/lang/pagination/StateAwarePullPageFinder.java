package com.sunnysuperman.mountain.lang.pagination;

public interface StateAwarePullPageFinder<K, T> extends StateAwareFinder<K> {

	PullPage<T> findForPullPage(K state, String marker, int limit);

}