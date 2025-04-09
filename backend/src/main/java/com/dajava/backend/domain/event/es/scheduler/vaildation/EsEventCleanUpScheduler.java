package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.ByQueryResponse;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EsEventCleanUpScheduler {

	private final ElasticsearchOperations elasticsearchOperations;

	@Value("${cleanup.scheduler.duration.log}")
	private String logDuration; // 단위: ms (ex: "3600000")

	@Value("${cleanup.soft-delete-day.log}")
	private int logDeleteDay;

	private static final List<String> INDEX_NAMES = List.of(
		"pointer-click-events",
		"pointer-move-events",
		"pointer-scroll-events",
		"session-data"
	);

	//@Scheduled(fixedRateString = "${cleanup.scheduler.duration.log}")
	public void deleteOldDocuments() {
		long nowMinus1Day = Instant.now().minus(logDeleteDay, ChronoUnit.DAYS).toEpochMilli();

		for (String indexName : INDEX_NAMES) {
			try {
				// Criteria 기반 쿼리 생성
				Criteria criteria = new Criteria("timestamp").lessThan(nowMinus1Day);

				// 모든 문서 삭제 (match all)
				//Query query = NativeQuery.builder()
				//	.withQuery(q -> q.matchAll(m -> m))
				//	.build();

				Query query = new CriteriaQuery(criteria);

				// deleteByQuery 실행
				ByQueryResponse response = elasticsearchOperations.delete(query, Object.class, IndexCoordinates.of(indexName));
				long deletedCount = response.getDeleted();

				log.info("[{}] 인덱스에서 오래된 click, move, scroll 문서 삭제 성공. 삭제된 문서 수: {}", indexName, deletedCount);
			} catch (Exception e) {
				log.error("[{}] 인덱스에서 오래된 click, move, scroll  문서 삭제 실패: {}", indexName, e.getMessage());
			}
		}
	}
}
