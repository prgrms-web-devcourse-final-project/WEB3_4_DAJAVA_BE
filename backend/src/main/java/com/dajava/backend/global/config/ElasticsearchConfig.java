package com.dajava.backend.global.config;

import java.net.URI;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticsearchConfig {

	@Value("${spring.elasticsearch.uris}")
	private String elasticsearchUri;

	@Bean
	public ElasticsearchClient elasticsearchClient() {
		// URI 문자열에서 호스트, 포트 추출
		URI uri = URI.create(elasticsearchUri);
		String host = uri.getHost();
		int port = uri.getPort();

		RestClient restClient = RestClient.builder(new HttpHost(host, port)).build();
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		return new ElasticsearchClient(transport);
	}
}
