package com.sunnysuperman.mountain.localcache;

public interface LocalCache<T> {

	T find();

	void update();

	void reload();

}
