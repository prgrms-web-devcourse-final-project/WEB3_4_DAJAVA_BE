package com.dajava.backend.domain.event.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.SessionDataKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityHandleService {
	private final EventBatchService eventBatchService;

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
