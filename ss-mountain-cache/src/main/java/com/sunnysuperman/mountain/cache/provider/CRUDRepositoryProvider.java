package com.sunnysuperman.mountain.cache.provider;

import java.util.Collection;
import java.util.Map;

import com.sunnysuperman.mountain.repository.CRUDRepository;

public class CRUDRepositoryProvider<T, K> implements RepositoryProvider<T, K> {
	CRUDRepository<T, K> repo;

	public CRUDRepositoryProvider(CRUDRepository<T, K> repo) {
		super();
		this.repo = repo;
	}

	@Override
	public T findByKey(K key) {
		return repo.findById(key);
	}

	@Override
	public Map<K, T> findByKeys(Collection<K> keys) {
		return repo.findForMapByIds(keys);
	}

}
