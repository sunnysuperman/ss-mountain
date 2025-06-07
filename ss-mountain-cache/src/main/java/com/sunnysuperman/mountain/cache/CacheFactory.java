package com.sunnysuperman.mountain.cache;

import java.util.function.Function;

import com.sunnysuperman.mountain.cache.converter.Converter;
import com.sunnysuperman.mountain.cache.converter.ObjectConverter;
import com.sunnysuperman.mountain.cache.provider.CRUDRepositoryProvider;
import com.sunnysuperman.mountain.cache.provider.CRUDRepositoryWithAssemblerProvider;
import com.sunnysuperman.mountain.cache.provider.RepositoryProvider;
import com.sunnysuperman.mountain.cache.provider.SingleRepositoryProvider;
import com.sunnysuperman.mountain.repository.CRUDRepository;

/** 缓存工厂 **/
public interface CacheFactory {

	<T, K> Cache<T, K> create(CacheOptions options, RepositoryProvider<T, K> repository, Converter<T> converter,
			CacheSaveFilter<T, K> saveFilter);

	default <T, K> Cache<T, K> create(CacheOptions options, RepositoryProvider<T, K> repository,
			Converter<T> converter) {
		return create(options, repository, converter, null);
	}

	default <T, K> Cache<T, K> create(CacheOptions options, RepositoryProvider<T, K> repo, Class<T> type) {
		return create(options, repo, new ObjectConverter<>(type));
	}

	default <T, K> Cache<T, K> create(CacheOptions options, CRUDRepository<T, K> repo, Class<T> type) {
		return create(options, new CRUDRepositoryProvider<>(repo), new ObjectConverter<>(type));
	}

	default <T, K, R> Cache<R, K> create(CacheOptions options, CRUDRepository<T, K> repo, Function<T, R> assembler,
			Class<R> type) {
		return create(options, repo, assembler, new ObjectConverter<>(type));
	}

	default <T, K, R> Cache<R, K> create(CacheOptions options, CRUDRepository<T, K> repo, Function<T, R> assembler,
			Converter<R> converter) {
		return create(options, new CRUDRepositoryWithAssemblerProvider<>(repo, assembler), converter);
	}

	default <T, K> Cache<T, K> createSingle(CacheOptions options, SingleRepositoryProvider<T, K> repo,
			Converter<T> converter) {
		return create(options, repo, converter);
	}

	default <T, K> Cache<T, K> createSingle(CacheOptions options, SingleRepositoryProvider<T, K> repo, Class<T> type) {
		return create(options, repo, new ObjectConverter<>(type));
	}

}
