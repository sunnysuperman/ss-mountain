package com.sunnysuperman.mountain.lang.pagination;

public interface StateAwareFinder<K> {

	K parseState(String str);

	K getInitState();

	K getNextState(K state);

	String serializeState(K state);

}