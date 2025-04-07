package com.dajava.backend.redis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.service.EventBatchService;
import com.dajava.backend.domain.event.service.SessionDataService;
import com.dajava.backend.redis.buffer.EventRedisBuffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EventBatchService 를 호출하며 플래그값을 같이 전달하는 서비스 입니다.
 *
 * @author Metronon
 * @since 2025-03-31
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityHandleServiceRedis {
	private final EventBatchService eventBatchServiceRedis;
	private final EventRedisBuffer eventRedisBuffer;

	/**
	 * 활성 세션 배치 처리 메서드
	 * isInactive 값이 false 로, 캐시가 제거되지 않습니다.
	 */
	@Transactional
	public void processActiveBatchForSession(SessionDataKey key) {
		log.info("활성 세션 배치 처리 요청 (Redis): {}", key);
		eventBatchServiceRedis.processBatchForSession(key, false);
	}

	/**
	 * 비활성 세선 배치 처리 메서드
	 * isInactive 값이 true 로, 캐시가 제거됩니다.
	 */
	@Transactional
	public void processInactiveBatchForSession(SessionDataKey key) {
		log.info("비활성 세션 배치 처리 요청 (Redis): {}", key);

		// 1. EventBatchService를 호출하여 Redis에서 데이터를 가져와 Elasticsearch/RDBMS에 저장하고,
		//    비활성 플래그를 전달하여 캐시 제거 및 세션 종료 처리
		eventBatchServiceRedis.processBatchForSession(key, true);

		// 2. Redis에서 해당 세션의 모든 이벤트 데이터 삭제
		long deletedCount = 0;
		deletedCount += eventRedisBuffer.flushClickEvents(key).size();
		deletedCount += eventRedisBuffer.flushMoveEvents(key).size();
		deletedCount += eventRedisBuffer.flushScrollEvents(key).size();
		log.info("비활성 세션 {} Redis 데이터 삭제 완료 ({}개 - flush 활용)", key, deletedCount);
	}
}
