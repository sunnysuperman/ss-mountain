package com.sunnysuperman.mountain.search;

import java.util.List;

public interface ResultWrapper<T, R> {

	List<R> wrap(List<T> items);

}