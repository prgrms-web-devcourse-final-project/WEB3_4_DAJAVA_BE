package com.dajava.backend.global.elasticsearch;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IndexService {

	private final ElasticsearchOperations elasticsearchOperations;

	public boolean deleteIndex(String indexName) {
		boolean exists = elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).exists();

		if (exists) {
			return elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).delete();
		} else {
			System.out.println("해당 인덱스가 존재하지 않습니다: " + indexName);
			return false;
		}
	}
}
