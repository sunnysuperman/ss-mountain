package com.sunnysuperman.mountain.cache.converter;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sunnysuperman.mountain.cache.CacheException;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.utils.Jsons;

public class PullPageConverter<T> implements Converter<PullPage<T>> {
	private Class<T> type;

	public PullPageConverter(Class<T> type) {
		super();
		this.type = type;
	}

	@Override
	public PullPage<T> deserialize(byte[] bytes) throws CacheException {
		return Jsons.readForParametricType(new String(bytes, StandardCharsets.UTF_8), PullPage.class,
				TypeFactory.defaultInstance().constructType(type));
	}

	@Override
	public byte[] serialize(PullPage<T> set) throws CacheException {
		return Jsons.writeAsBytes(set);
	}

}
