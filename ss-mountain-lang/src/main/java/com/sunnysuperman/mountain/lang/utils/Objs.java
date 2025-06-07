package com.sunnysuperman.mountain.lang.utils;

/** utils包内调用（考虑到Obj类引入可选的Spring依赖） **/
class Objs {

	private Objs() {
	}

	public static <T> T or(T obj, T defaults) {
		if (obj != null) {
			return obj;
		}
		return defaults;
	}

}
