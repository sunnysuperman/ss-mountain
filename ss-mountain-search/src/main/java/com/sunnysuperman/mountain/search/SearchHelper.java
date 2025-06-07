package com.sunnysuperman.mountain.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;

import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.lang.exception.Exceptions;
import com.sunnysuperman.mountain.lang.pagination.MarkerUtils;
import com.sunnysuperman.mountain.lang.pagination.Page;
import com.sunnysuperman.mountain.lang.pagination.PullPage;
import com.sunnysuperman.mountain.lang.pagination.PullPageRequest;
import com.sunnysuperman.mountain.lang.utils.Num;
import com.sunnysuperman.mountain.lang.utils.Str;

public class SearchHelper {
	private static final Logger LOG = LoggerFactory.getLogger(SearchHelper.class);

	@Resource
	private ElasticsearchRestTemplate elasticsearchRestTemplate;

	private String indexPrefix;

	public SearchHelper(ElasticsearchRestTemplate esTemplate, SearchProperties searchProperties, EnvHelper envHelper) {
		super();
		elasticsearchRestTemplate = esTemplate;
		indexPrefix = makeIndexPrefix(searchProperties, envHelper);
		LOG.info(">>>>>>[search] initialized, index-prefix: '{}'", indexPrefix);
	}

	private String makeIndexPrefix(SearchProperties searchProperties, EnvHelper envHelper) {
		StringBuilder b = new StringBuilder();
		if (searchProperties.isUseProfileAsNamespace()) {
			b.append(Str.or(searchProperties.getProfile(), envHelper.getProfile())).append('-');
		}
		if (searchProperties.isUseAppNameAsNamespace()) {
			b.append(Str.or(searchProperties.getAppName(), envHelper.getApplicationName())).append('-');
		}
		return b.toString();
	}

	public void save(Object index) {
		elasticsearchRestTemplate.save(index, getIndexCoordinates(index.getClass()));
	}

	public void saveMany(List<?> indexes) {
		if (indexes.isEmpty()) {
			return;
		}
		if (indexes.size() == 1) {
			save(indexes.get(0));
			return;
		}
		elasticsearchRestTemplate.save(indexes, getIndexCoordinates(indexes.get(0).getClass()));
	}

	public void delete(String id, Class<?> clazz) {
		elasticsearchRestTemplate.delete(id, getIndexCoordinates(clazz));
	}

	public <T> void deleteAll(Class<T> clazz) {
		while (true) {
			NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
			queryBuilder.withPageable(PageRequest.of(0, 1000));
			NativeSearchQuery query = queryBuilder.build();
			SearchHits<T> hits = elasticsearchRestTemplate.search(query, clazz, getIndexCoordinates(clazz));
			if (hits.getTotalHits() == 0) {
				break;
			}
			List<SearchHit<T>> items = hits.getSearchHits();
			for (SearchHit<T> item : items) {
				elasticsearchRestTemplate.delete(Objects.requireNonNull(item.getId()), getIndexCoordinates(clazz));
			}
		}
	}

	public long countAll(Class<?> clazz) {
		return elasticsearchRestTemplate.count(new StringQuery(QueryBuilders.matchAllQuery().toString()),
				getIndexCoordinates(clazz));
	}

	public long count(Query query, Class<?> clazz) {
		return elasticsearchRestTemplate.count(query, clazz, getIndexCoordinates(clazz));
	}

	public <T, R> List<R> searchForList(NativeSearchQueryBuilder queryBuilder, int limit, Class<T> clazz,
			ResultWrapper<T, R> wrapper) {
		queryBuilder.withPageable(PageRequest.of(0, limit));

		SearchHits<T> hits = elasticsearchRestTemplate.search(queryBuilder.build(), clazz, getIndexCoordinates(clazz));
		List<SearchHit<T>> rawItems = hits.getSearchHits();
		if (rawItems.isEmpty()) {
			return Collections.emptyList();
		}

		List<T> items = new ArrayList<>(rawItems.size());
		for (SearchHit<T> item : rawItems) {
			items.add(item.getContent());
		}
		try {
			return wrapper.wrap(items);
		} catch (Exception e) {
			throw Exceptions.wrapRuntimeException(e);
		}
	}

	public <T, R> PullPage<R> searchForPullPage(NativeSearchQueryBuilder queryBuilder, PullPageRequest pullPageReq,
			Class<T> clazz, ResultWrapper<T, R> wrapper) {
		return searchForPullPage(queryBuilder, pullPageReq.getMarker(), pullPageReq.getLimit(), clazz, wrapper);
	}

	public <T, R> PullPage<R> searchForPullPage(NativeSearchQueryBuilder queryBuilder, String marker, int limit,
			Class<T> clazz, ResultWrapper<T, R> wrapper) {
		int offset = MarkerUtils.parseOffset(marker);
		queryBuilder.withPageable(PageRequest.of(offset / limit, limit));

		SearchHits<T> hits = elasticsearchRestTemplate.search(queryBuilder.build(), clazz, getIndexCoordinates(clazz));
		List<SearchHit<T>> rawItems = hits.getSearchHits();
		if (rawItems.isEmpty()) {
			return PullPage.empty();
		}

		List<T> items = new ArrayList<>(rawItems.size());
		for (SearchHit<T> item : rawItems) {
			items.add(item.getContent());
			if (items.size() >= limit) {
				break;
			}
		}
		List<R> convertedItems;
		try {
			convertedItems = wrapper.wrap(items);
		} catch (Exception e) {
			throw Exceptions.wrapRuntimeException(e);
		}
		boolean hasMore = rawItems.size() >= limit;

		return PullPage.of(convertedItems, String.valueOf(offset + limit), hasMore);
	}

	public <T, R> Page<R> searchForPage(NativeSearchQueryBuilder queryBuilder, int offset, int limit, Class<T> clazz,
			ResultWrapper<T, R> wrapper) {
		queryBuilder.withPageable(PageRequest.of(offset / limit, limit));

		SearchHits<T> hits = elasticsearchRestTemplate.search(queryBuilder.build(), clazz, getIndexCoordinates(clazz));
		int totalCount = Num.long2int(hits.getTotalHits());
		List<SearchHit<T>> rawItems = hits.getSearchHits();
		if (rawItems.isEmpty()) {
			return new Page<>(Collections.emptyList(), totalCount, offset, limit);
		}

		List<T> items = new ArrayList<>(rawItems.size());
		for (SearchHit<T> item : rawItems) {
			items.add(item.getContent());
		}
		List<R> convertedItems;
		try {
			convertedItems = wrapper.wrap(items);
		} catch (Exception e) {
			throw Exceptions.wrapRuntimeException(e);
		}
		return new Page<>(convertedItems, totalCount, offset, limit);
	}

	public QueryBuilder wrapTextQuery(String key, String text) {
		if (text == null || text.isEmpty()) {
			throw new IllegalArgumentException("Require text");
		}
		return QueryBuilders.matchPhraseQuery(key, text);
	}

	private IndexCoordinates getIndexCoordinates(Class<?> clazz) {
		String indexName = clazz.getAnnotation(Document.class).indexName();
		if (!indexPrefix.isEmpty()) {
			indexName = indexPrefix + indexName;
		}
		return IndexCoordinates.of(indexName);
	}
}