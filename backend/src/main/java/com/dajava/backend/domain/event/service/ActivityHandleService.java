package com.dajava.backend.domain.event.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.SessionDataKey;

import lombok.RequiredArgsConstructor;

/**
 * EventBatchService 를 호출하며 플래그값을 같이 전달하는 서비스 입니다.
 *
 * @author Metronon
 * @since 2025-03-31
 */
@Service
@RequiredArgsConstructor
public class ActivityHandleService {
	private final EventBatchService eventBatchService;

	/**
	 * 활성 세션 배치 처리 메서드
	 * isInactive 값이 false 로, 캐시가 제거되지 않습니다.
	 */
	@Transactional
	public void processActiveBatchForSession(SessionDataKey key) {
		eventBatchService.processBatchForSession(key, false);
	}

	/**
	 * 비활성 세선 배치 처리 메서드
	 * isInactive 값이 true 로, 캐시가 제거됩니다.
	 */
	@Transactional
	public void processInactiveBatchForSession(SessionDataKey key) {
		eventBatchService.processBatchForSession(key, true);
	}
}
