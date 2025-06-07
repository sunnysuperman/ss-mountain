package com.sunnysuperman.mountain.cache.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.sunnysuperman.mountain.repository.CRUDRepository;

public class CRUDRepositoryWithAssemblerProvider<T, K, R> implements RepositoryProvider<R, K> {
	CRUDRepository<T, K> repo;
	Function<T, R> assembler;

	public CRUDRepositoryWithAssemblerProvider(CRUDRepository<T, K> repo, Function<T, R> assembler) {
		super();
		this.repo = repo;
		this.assembler = assembler;
	}

	@Override
	public R findByKey(K key) {
		return assembler.apply(repo.findById(key));
	}

	@Override
	public Map<K, R> findByKeys(Collection<K> keys) {
		Map<K, T> map = repo.findForMapByIds(keys);
		if (map.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<K, R> convertedMap = new HashMap<>();
		map.forEach((k, v) -> {
			R convertedValue = assembler.apply(v);
			if (convertedValue != null) {
				convertedMap.put(k, convertedValue);
			}
		});
		return convertedMap;
	}

}
