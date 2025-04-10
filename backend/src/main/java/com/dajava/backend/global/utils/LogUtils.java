package com.dajava.backend.global.utils;

import static com.dajava.backend.global.exception.ErrorCode.*;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;
import com.dajava.backend.domain.log.exception.LogException;

/**
 * Redis 키 생성을 위한 로그 유틸리티 클래스
 */
public class LogUtils {
	private static final String DELIMITER = "|";
	private static final int EXPECTED_PARTS = 3;

	/**
	 * SessionIdentifier 객체를 Redis 키 문자열로 변환
	 * 예: "sessionId|pageUrl|memberSerialNumber"
	 *
	 * @param identifier 세션 식별자
	 * @return Redis 키 문자열
	 * @throws LogException sessionIdentifier가 null일 경우
	 */
	public static String createRedisKey(SessionIdentifier identifier) {
		if (identifier == null) {
			throw new LogException(SESSION_IDENTIFIER_NOT_FOUND);
		}
		return String.join(DELIMITER,
			identifier.getSessionId(),
			identifier.getPageUrl(),
			identifier.getMemberSerialNumber());
	}

	/**
	 * Redis 키 문자열을 SessionIdentifier 객체로 변환
	 *
	 * @param key Redis 키 문자열
	 * @return SessionIdentifier 객체
	 * @throws LogException key가 null이거나 형식이 잘못된 경우
	 */
	public static SessionIdentifier parseRedisKey(String key) {
		if (key == null || key.isBlank()) {
			throw new LogException(SESSION_IDENTIFIER_PARSING_ERROR);
		}
		String[] parts = key.split("\\|");

		if (parts.length != EXPECTED_PARTS) {
			throw new LogException(SESSION_IDENTIFIER_PARSING_ERROR);
		}
		return new SessionIdentifier(parts[0], parts[1], parts[2]);
	}
}
