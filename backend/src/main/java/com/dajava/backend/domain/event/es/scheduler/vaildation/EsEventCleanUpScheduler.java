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
		Instant now = Instant.now();
		Instant deleteBefore = now.minus(logDeleteDay, ChronoUnit.DAYS);

		// 시간 단위로 24개 구간으로 쪼개기
		for (int i = 0; i < 24; i++) {
			Instant from = deleteBefore.plus(i, ChronoUnit.HOURS);
			Instant to = deleteBefore.plus(i + 1, ChronoUnit.HOURS);

			long fromMillis = from.toEpochMilli();
			long toMillis = to.toEpochMilli();

			for (String indexName : INDEX_NAMES) {
				try {
					Criteria criteria = new Criteria("timestamp")
						.greaterThanEqual(fromMillis)
						.lessThan(toMillis);

					Query query = new CriteriaQuery(criteria);

					ByQueryResponse response = elasticsearchOperations.delete(query, Object.class,
						IndexCoordinates.of(indexName));
					long deletedCount = response.getDeleted();

					log.info("[{}] 인덱스에서 [{} ~ {}] 범위의 click, move, scroll 문서 삭제 성공. 삭제된 문서 수: {}",
						indexName, from, to, deletedCount
					);
				} catch (Exception e) {
					log.error("[{}] 인덱스에서 [{} ~ {}] click, move, scroll 문서 삭제 실패: {}",
						indexName, from, to, e.getMessage()
					);
				}
			}
		}
	}
}