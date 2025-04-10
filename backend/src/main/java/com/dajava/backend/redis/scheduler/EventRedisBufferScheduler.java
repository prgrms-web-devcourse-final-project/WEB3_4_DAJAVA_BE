package com.dajava.backend.redis.scheduler;

import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.global.component.analyzer.BufferSchedulerProperties;
import com.dajava.backend.global.utils.SessionDataKeyUtils;
import com.dajava.backend.redis.buffer.EventRedisBuffer;
import com.dajava.backend.domain.log.service.RedisActivityHandleService;
import com.dajava.backend.domain.log.service.RedisEventBatchService;

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
public class EventRedisBufferScheduler {

	private final RedisEventBatchService redisEventBatchService;
	private final EventRedisBuffer eventRedisBuffer;
	private final BufferSchedulerProperties properties;
	private final RedisActivityHandleService redisActivityHandleService;

	/**
	 * 1분마다 실행되어 비활성 세션을 감지하고 처리합니다.
	 * 마지막 활동 시간이 기준 시간(10분)을 초과한 세션의 데이터를
	 * 저장하고 버퍼와 캐시에서 제거합니다.
	 * secret yml 을 통해 주기를 조정할 수 있습니다.
	 */
	@Scheduled(fixedRateString = "#{@bufferSchedulerProperties.inactiveSessionDetectThresholdMs}")
	public void flushInactiveEventBuffers() {
		log.info("비활성 세션 처리 작업 시작");
		long now = System.currentTimeMillis();
		Set<SessionDataKey> activeKeys = eventRedisBuffer.getAllActiveSessionKeys();
		int inactiveCount = 0;
		for (SessionDataKey sessionKey : activeKeys) {
			String key = SessionDataKeyUtils.toKey(sessionKey);
			// 세션의 마지막 활동 시간 확인
			Long lastClickUpdate = eventRedisBuffer.getClickBuffer().getLastUpdated(key);
			Long lastMoveUpdate = eventRedisBuffer.getMoveBuffer().getLastUpdated(key);
			Long lastScrollUpdate = eventRedisBuffer.getScrollBuffer().getLastUpdated(key);
			// 가장 최근 업데이트 시간 계산
			Long latestUpdate = getLatestUpdate(lastClickUpdate, lastMoveUpdate, lastScrollUpdate);
			// 비활성 세션 여부 확인
			if (latestUpdate == null || (now - latestUpdate) >= properties.getInactiveThresholdMs()) {
				log.info("비활성 세션 감지: {}", sessionKey);
				inactiveCount++;
				// 배치 처리를 통해 데이터 저장 및 캐시 제거
				redisActivityHandleService.processInactiveBatchForSession(sessionKey);
			}
		}

		log.info("비활성 세션 처리 완료: 총 {}개 세션 처리됨", inactiveCount);
	}

	/**
	 * 5분마다 실행되어 모든 활성 세션의 데이터를 주기적으로 저장합니다.
	 * 세션의 활성 상태와 관계없이 현재 버퍼에 있는 모든 세션 데이터를
	 * 처리하여 데이터 손실 위험을 줄입니다.
	 * secret yml 을 통해 주기를 조정할 수 있습니다.
	 */
	@Scheduled(fixedRateString = "#{@bufferSchedulerProperties.activeSessionFlushIntervalMs}")
	public void flushAllEventBuffers() {
		log.info("모든 활성 세션 정기 처리 작업 시작");

		Set<SessionDataKey> activeKeys = eventRedisBuffer.getAllActiveSessionKeys();
		log.info("처리할 활성 세션 수: {}", activeKeys.size());

		for (SessionDataKey sessionKey : activeKeys) {
			try {
				redisActivityHandleService.processActiveBatchForSession(sessionKey);
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

