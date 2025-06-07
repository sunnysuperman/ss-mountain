package com.sunnysuperman.mountain.lang.assembler;

import java.util.List;
import java.util.Map;

public interface ListAssembler<T, T2> {

	default Object beforeAssembling(List<T> list, Map<String, Object> context) {
		return null;
	}

	void assemble(T before, T2 after, Object intermediate, Map<String, Object> context);

	default void afterAssembling(List<T> list, List<T2> result, Map<String, Object> context) {
		// nope
	}

}
