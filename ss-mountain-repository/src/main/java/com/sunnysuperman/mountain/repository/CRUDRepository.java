package com.sunnysuperman.mountain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CRUDRepository<T, I> {

	SaveResult save(T entity) throws RepositoryException;

	void insert(T entity) throws RepositoryException;

	void insertBatch(List<T> entityList) throws RepositoryException;

	boolean update(T entity) throws RepositoryException;

	boolean update(T entity, Set<String> fields) throws RepositoryException;

	boolean updateBatch(List<T> entityList) throws RepositoryException;

	boolean updateBatch(List<T> entityList, Set<String> fields) throws RepositoryException;

	void compareAndUpdateVersion(T entity) throws RepositoryException;

	boolean deleteById(I id) throws RepositoryException;

	int deleteByIds(Collection<I> ids) throws RepositoryException;

	boolean delete(T entity) throws RepositoryException;

	boolean deleteBatch(List<T> entityList) throws RepositoryException;

	boolean existsById(I id) throws RepositoryException;

	T findById(I id) throws RepositoryException;

	T getById(I id) throws RepositoryException;

	List<T> findByIds(Collection<I> ids) throws RepositoryException;

	List<T> findByIdsInOrder(Collection<I> ids) throws RepositoryException;

	Map<I, T> findForMapByIds(Collection<I> ids) throws RepositoryException;

	List<T> findAll() throws RepositoryException;
}
