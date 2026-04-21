package com.sunnysuperman.mountain.cache.converter;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectReader;
import com.sunnysuperman.mountain.cache.CacheException;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.utils.Jsons;

public class PullPageConverter<T> implements Converter<PullPage<T>> {

	private ObjectReader reader;

	public PullPageConverter(Class<T> type) {
		super();
		reader = Jsons.getReader(PullPage.class, type);
	}

	@Override
	public PullPage<T> deserialize(byte[] bytes) throws CacheException {
		try {
			return reader.readValue(bytes);
		} catch (IOException e) {
			throw new CacheException(e);
		}
	}

	@Override
	public byte[] serialize(PullPage<T> obj) throws CacheException {
		return Jsons.writeAsBytes(obj);
	}

}
