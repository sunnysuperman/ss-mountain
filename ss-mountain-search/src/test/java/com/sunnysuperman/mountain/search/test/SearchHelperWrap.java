package com.sunnysuperman.mountain.search.test;

import com.sunnysuperman.mountain.lang.utils.ProcessUtil;
import com.sunnysuperman.mountain.search.SearchHelper;

public class SearchHelperWrap {
	SearchHelper helper;

	public long countAll(Class<?> clazz) {
		return helper.countAll(clazz);
	}

	public void deleteAll(Class<?> clazz) {
		helper.deleteAll(clazz);
		while (countAll(clazz) != 0) {
			ProcessUtil.sleep(1000);
		}
	}

	public SearchHelper getHelper() {
		return helper;
	}
}
