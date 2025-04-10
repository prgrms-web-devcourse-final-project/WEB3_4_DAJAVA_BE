package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.global.component.analyzer.CleanUpSchedulerProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 주기적으로 이벤트 데이터 삭제 로직을 실행하는 스케줄러 입니다.
 * es의 데이터를 조건에 맞춰 삭제합니다.
 * 추 후 테스트 코드 작성 필요
 * @author NohDongHui
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EsEventCleanUpScheduler {

	private final ElasticsearchOperations elasticsearchOperations;

	private final CleanUpSchedulerProperties cleanUpProps;

	private final RegisterRepository registerRepository;

	@Value("${cleanup.scheduler.duration.log}")
	private String logDuration; // 단위: ms (ex: "3600000")


	private static final List<String> INDEX_NAMES = List.of(
		"pointer-click-events",
		"pointer-move-events",
		"pointer-scroll-events",
		"session-data"
	);

	private static final String SOLUTION_EVENTS = "solution-event";

	/**
	 * 한번에 삭제 하지 않고 24시간 분할해 이벤트 로그 데이터를 삭제하는 스케줄러
	 * es의 데이터를 timestamp 에 맞춰 삭제합니다.
	 *
	 */
	//@Scheduled(fixedRateString = "#{@cleanUpSchedulerProperties.schedulerDuration.log}")
	public void deleteOldEventDocuments() {

		// 얼마나 오래된 데이터 삭제할지 현재 2일전 ~ 1일전 데이터 삭제
		int logDeleteDay= cleanUpProps.getSoftDeleteDay().getLog() + 1;

		Instant now = Instant.now();
		Instant deleteBefore = now.minus(logDeleteDay, ChronoUnit.DAYS);
		Instant dayNPlus1 = now.minus(logDeleteDay + 1, ChronoUnit.DAYS);

		// 혹시 지워지지 않은 오래된 데이터 삭제
		for (String indexName : INDEX_NAMES) {
			deleteOldEventDocsBefore(indexName, dayNPlus1.toEpochMilli(), dayNPlus1);
		}

		// 시간 단위로 24개 구간으로 쪼개기, 2일전 ~ 1일전 데이터 삭제
		for (int i = 0; i < 24; i++) {
			Instant from = deleteBefore.plus(i, ChronoUnit.HOURS);
			Instant to = deleteBefore.plus(i + 1, ChronoUnit.HOURS);

			long fromMillis = from.toEpochMilli();
			long toMillis = to.toEpochMilli();

			for (String indexName : INDEX_NAMES) {
				deleteEventDocsByTimeRange(indexName, fromMillis, toMillis, from, to);
			}
		}
	}

	/**
	 * 오래된 솔루션 이벤트를 삭제하는 스케줄러
	 */
	//@Scheduled(fixedRateString = "#{@cleanUpSchedulerProperties.schedulerDuration.solution}")
	public void deleteOldSolutiontEventDocuments() {

		// 얼마나 오래된 데이터 삭제할지
		int logDeleteDay = cleanUpProps.getSoftDeleteDay().getSolution();
		LocalDateTime threshold = LocalDateTime.now().minusDays(logDeleteDay);

		// 삭제 대상 register 조회
		List<Register> completedRegisters = registerRepository.findAllCompletedRegisterList(threshold);

		if (completedRegisters.isEmpty()) {
			log.info("삭제할 SolutionEventDocument가 없습니다 ");
			return;
		}

		Instant now = Instant.now();
		Instant deleteBefore = now.minus(logDeleteDay, ChronoUnit.DAYS);
		Instant dayNPlus1 = now.minus(logDeleteDay + 1, ChronoUnit.DAYS);

		for (Register register : completedRegisters) {
			String serialNumber = register.getSerialNumber();

			//혹시 지워지지 않은 그전 데이터 삭제
			deleteOldSolutionDocsBefore(serialNumber, dayNPlus1.toEpochMilli(), dayNPlus1);

			// 24시간으로 분할해 삭제
			for (int i = 0; i < 24; i++) {
				Instant from = deleteBefore.plus(i, ChronoUnit.HOURS);
				Instant to = deleteBefore.plus(i + 1, ChronoUnit.HOURS);

				long fromMillis = from.toEpochMilli();
				long toMillis = to.toEpochMilli();

				deleteSolutionDocsBySerialAndRange(register.getSerialNumber(), fromMillis, toMillis, from, to);

			}
		}
	}

	/**
	 * 인덱스의 타임스탬프 범위내 로그 이벤트 데이터 삭제
	 * @param indexName
	 * @param fromMillis
	 * @param toMillis
	 * @param from
	 * @param to
	 */
	private void deleteEventDocsByTimeRange(String indexName, long fromMillis, long toMillis, Instant from, Instant to) {
		try {
			Criteria criteria = new Criteria("timestamp")
				.greaterThanEqual(fromMillis)
				.lessThan(toMillis);

			Query query = new CriteriaQuery(criteria);

			ByQueryResponse response = elasticsearchOperations.delete(query, Object.class, IndexCoordinates.of(indexName));
			long deletedCount = response.getDeleted();

			log.info("[{}] 인덱스에서 [{} ~ {}] 범위의 문서 삭제 성공. 삭제된 문서 수: {}",
				indexName, from, to, deletedCount);
		} catch (Exception e) {
			log.error("[{}] 인덱스에서 [{} ~ {}] 문서 삭제 실패: {}",
				indexName, from, to, e.getMessage());
		}
	}

	/**
	 * 일련번호에 해당하는 timestamp 범위내 솔루션 이벤트 데이터 삭제
	 * @param serialNumber
	 * @param fromMillis
	 * @param toMillis
	 * @param from
	 * @param to
	 */
	private void deleteSolutionDocsBySerialAndRange(String serialNumber, long fromMillis, long toMillis, Instant from, Instant to) {
		try {
			Criteria criteria = new Criteria("serialNumber").is(serialNumber)
				.and(new Criteria("timestamp").greaterThanEqual(fromMillis).lessThan(toMillis));

			Query query = new CriteriaQuery(criteria);

			ByQueryResponse response = elasticsearchOperations.delete(query, Object.class, IndexCoordinates.of("solution-event"));
			long deletedCount = response.getDeleted();

			log.info("[solution-event] serialNumber [{}]의 [{} ~ {}] 구간 문서 삭제 성공 - {}건",
				serialNumber, from, to, deletedCount);
		} catch (Exception e) {
			log.error("[solution-event] serialNumber [{}]의 [{} ~ {}] 구간 문서 삭제 실패: {}",
				serialNumber, from, to, e.getMessage());
		}
	}

	/**
	 * 통 삭제용 메서드 혹시 지워지지 않은 그전 이벤트 로그 데이터를 삭제합니다.
	 * @param indexName
	 * @param timestampMillis
	 * @param timestampInstant
	 */
	private void deleteOldEventDocsBefore(String indexName, long timestampMillis, Instant timestampInstant) {
		try {
			Criteria criteria = new Criteria("timestamp").lessThan(timestampMillis);
			Query query = new CriteriaQuery(criteria);

			ByQueryResponse response = elasticsearchOperations.delete(query, Object.class, IndexCoordinates.of(indexName));

			log.info("[{}] 인덱스에서 [{} 이전] 문서 전체 삭제 성공. 삭제된 문서 수: {}",
				indexName, timestampInstant, response.getDeleted());
		} catch (Exception e) {
			log.error("[{}] 인덱스에서 [{} 이전] 문서 삭제 실패: {}",
				indexName, timestampInstant, e.getMessage());
		}
	}

	/**
	 * 통 삭제용 메서드 혹시 지워지지 않은 그전 솔루션 이벤트 데이터를 삭제합니다.
	 * @param serialNumber
	 * @param timestampMillis
	 * @param timestampInstant
	 */
	private void deleteOldSolutionDocsBefore(String serialNumber, long timestampMillis, Instant timestampInstant) {
		try {
			Criteria criteria = new Criteria("serialNumber").is(serialNumber)
				.and(new Criteria("timestamp").lessThan(timestampMillis));
			Query query = new CriteriaQuery(criteria);

			ByQueryResponse response = elasticsearchOperations.delete(query, Object.class, IndexCoordinates.of("solution-event"));
			log.info("[solution-event] serialNumber [{}]의 [{} 이전] 문서 삭제 성공 - {}건",
				serialNumber, timestampInstant, response.getDeleted());
		} catch (Exception e) {
			log.error("[solution-event] serialNumber [{}]의 [{} 이전] 문서 삭제 실패: {}",
				serialNumber, timestampInstant, e.getMessage());
		}
	}

}