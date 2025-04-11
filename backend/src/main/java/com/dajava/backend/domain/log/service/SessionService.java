package com.dajava.backend.domain.log.service;

import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

public interface SessionService {
	/**
	 * 활성 세션 종료
	 * 세션이 종료됩니다.
	 */
	void expireSession(String sessionId);
	/**
	 * 활성 세션 배치 처리
	 * isInactive 값이 false 로, 캐시가 제거되지 않습니다.
	 */
	void SessionFlagActive(SessionIdentifier sessionIdentifier);
	/**
	 * 비활성 세선 배치 처리
	 * isInactive 값이 true 로, 캐시가 제거됩니다.
	 */
	void SessionFlagInActive(SessionIdentifier sessionIdentifier);
}
