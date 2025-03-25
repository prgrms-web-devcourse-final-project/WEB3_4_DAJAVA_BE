package com.dajava.backend.domain.event.scheduler;

import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.service.EventBatchService;
import com.dajava.backend.domain.event.service.EventLogService;
import com.dajava.backend.global.component.buffer.EventBuffer;
import com.dajava.backend.global.util.SessionDataKeyUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 버퍼 내부 데이터를 처리하는 스케줄러 입니다.
 * 주기적으로 버퍼내 데이터를 리포지드로 전송합니다.
 * 1. 비활성 세션의 처리 : 마지막 활동 시간 기준 10분이 경과하면 세션 데이터를 최종 저장 및 캐시에서 제거
 * 2. 활성 세션의 주기적 처리 : 활성 상태의 세션 데이터로 주기적으로 저장
 * @author nodonghui, Metronon
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EventBufferScheduler {

	// 비활성 상태 간주 시간 (10분)
	private static final long INACTIVITY_THRESHOLD_MS = 10 * 60 * 1000;

	// 활성 상태 세션 주기적 저장 주기 (5분)
	private static final long ACTIVE_SESSION_FLUSH_INTERVAL_MS = 5 * 60 * 1000;

	private final EventLogService eventLogService;
	private final EventBatchService eventBatchService;
	private final EventBuffer eventBuffer;

	/**
	 * 1분마다 실행되어 비활성 세션을 감지하고 처리합니다.
	 * 마지막 활동 시간이 기준 시간(10분)을 초과한 세션의 데이터를
	 * 저장하고 버퍼와 캐시에서 제거합니다.
	 */
	@Scheduled(fixedRate = 60_000) // 1분마다 실행
	public void flushInactiveEventBuffers() {
		log.info("비활성 세션 처리 작업 시작");
		long now = System.currentTimeMillis();

		Set<SessionDataKey> activeKeys = eventBuffer.getAllActiveSessionKeys();
		int inactiveCount = 0;

		for (SessionDataKey sessionKey : activeKeys) {
			String key = SessionDataKeyUtils.toKey(sessionKey);

			// 세션의 마지막 활동 시간 확인
			Long lastClickUpdate = eventBuffer.getClickBuffer().getLastUpdatedMap().get(key);
			Long lastMoveUpdate = eventBuffer.getMoveBuffer().getLastUpdatedMap().get(key);
			Long lastScrollUpdate = eventBuffer.getScrollBuffer().getLastUpdatedMap().get(key);

			// 가장 최근 업데이트 시간 계산
			Long latestUpdate = getLatestUpdate(lastClickUpdate, lastMoveUpdate, lastScrollUpdate);

			// 비활성 세션 여부 확인
			if (latestUpdate == null || (now - latestUpdate) >= INACTIVITY_THRESHOLD_MS) {
				log.info("비활성 세션 감지: {}", sessionKey);
				inactiveCount++;

				// 배치 처리를 통해 데이터 저장 및 캐시 제거
				eventBatchService.processBatchForSession(sessionKey);
			}
		}

		log.info("비활성 세션 처리 완료: 총 {}개 세션 처리됨", inactiveCount);
	}

	/**
	 * 5분마다 실행되어 모든 활성 세션의 데이터를 주기적으로 저장합니다.
	 * 세션의 활성 상태와 관계없이 현재 버퍼에 있는 모든 세션 데이터를
	 * 처리하여 데이터 손실 위험을 줄입니다.
	 */
	@Scheduled(fixedRate = ACTIVE_SESSION_FLUSH_INTERVAL_MS) // 5분마다 실행
	public <T> void flushAllEventBuffers() {
		log.info("모든 활성 세션 정기 처리 작업 시작");

		Set<SessionDataKey> activeKeys = eventBuffer.getAllActiveSessionKeys();
		log.info("처리할 활성 세션 수: {}", activeKeys.size());

		for (SessionDataKey sessionKey : activeKeys) {
			try {
				eventBatchService.processBatchForSession(sessionKey);
			} catch (Exception e) {
				log.error("세션 {} 처리 중 오류 발생: {}", sessionKey, e.getMessage(), e);
			}
		}

		log.info("모든 활성 세션 정기 처리 완료");
	}

	/**
	 * 주어진 업데이트 시간들 중 가장 최근 값을 반환합니다.
	 * 모든 값이 null인 경우 null을 반환합니다.
	 */
	private Long getLatestUpdate(Long... updates) {
		Long latest = null;

		for (Long update : updates) {
			if (update != null && (latest == null || update > latest)) {
				latest = update;
			}
		}

		return latest;
	}
}

