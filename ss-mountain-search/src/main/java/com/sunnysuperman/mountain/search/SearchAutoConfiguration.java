package com.sunnysuperman.mountain.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import com.sunnysuperman.mountain.base.EnvHelper;
import com.sunnysuperman.mountain.search.SearchProperties.EsProperties;

@Configuration(proxyBeanMethods = false)
public class SearchAutoConfiguration {

	@Bean
	@ConfigurationProperties("ss-mountain.search")
	public SearchProperties searchProperties() {
		return new SearchProperties();
	}

	@SuppressWarnings("java:S2095")
	@Bean
	public RestHighLevelClient elasticsearchClient(SearchProperties properties) {
		EsProperties esProps = properties.getEs();
		return RestClients.create(ClientConfiguration.builder().connectedTo(esProps.getUris().toArray(new String[0]))
				.withBasicAuth(esProps.getUsername(), esProps.getPassword()).build()).rest();
	}

	@Bean
	public ElasticsearchRestTemplate elasticsearchTemplate(RestHighLevelClient client) {
		return new ElasticsearchRestTemplate(client);
	}

	@Bean
	public SearchHelper searchHelper(ElasticsearchRestTemplate esTemplate, SearchProperties properties,
			EnvHelper envHelper) {
		return new SearchHelper(esTemplate, properties, envHelper);
	}

}
