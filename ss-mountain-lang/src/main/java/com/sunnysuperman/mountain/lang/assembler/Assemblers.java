package com.sunnysuperman.mountain.lang.assembler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.sunnysuperman.mountain.lang.utils.Obj;
import com.sunnysuperman.mountain.lang.utils.Types;

public class Assemblers<T, T2> {
	private List<ListAssembler<T, T2>> wrappers;
	private Map<String, Object> context = new HashMap<>();

	@SafeVarargs
	public Assemblers(ListAssembler<T, T2>... wrappers) {
		super();
		this.wrappers = List.of(wrappers);
	}

	public Assemblers(List<ListAssembler<T, T2>> wrappers) {
		super();
		this.wrappers = Objects.requireNonNull(wrappers);
	}

	public List<T2> assemble(List<T> list, Class<T2> destType, boolean copyByDefaults) {
		if (list.isEmpty()) {
			return Collections.emptyList();
		}
		List<Object> intermediates = new ArrayList<>(wrappers.size());
		wrappers.forEach(wrapper -> intermediates.add(wrapper.beforeAssembling(list, context)));
		List<T2> result = list.stream().map(item -> {
			T2 dest = Types.newInstance(destType);
			if (copyByDefaults) {
				Obj.copyProperties(item, dest);
			}
			for (int i = 0; i < wrappers.size(); i++) {
				wrappers.get(i).assemble(item, dest, intermediates.get(i), context);
			}
			return dest;
		}).collect(Collectors.toList());
		wrappers.forEach(wrapper -> wrapper.afterAssembling(list, result, context));
		return result;
	}

}
