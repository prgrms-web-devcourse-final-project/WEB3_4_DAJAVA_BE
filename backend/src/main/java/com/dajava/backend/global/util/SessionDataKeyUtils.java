package com.dajava.backend.global.util;

import com.dajava.backend.domain.event.dto.SessionDataKey;

public final class SessionDataKeyUtils {

	private SessionDataKeyUtils() {
		// 유틸 클래스는 생성 불가하게 막기
	}

	/**
	 * "id|url|memberNumber" 형태의 key 문자열을 SessionDataKey 객체로 변환합니다.
	 * 잘못된 형식일 경우 null을 반환합니다.
	 *
	 * @param key 버퍼 내부 key 문자열
	 * @return SessionDataKey 또는 null
	 */
	public static SessionDataKey parseKey(String key) {
		if (key == null || key.isEmpty()) {
			return null;
		}

		String[] parts = key.split("\\|");
		if (parts.length != 3) {
			return null;
		}

		String sessionId = parts[0];
		String pageUrl = parts[1];
		String memberSerialNumber = parts[2];

		return new SessionDataKey(sessionId, pageUrl, memberSerialNumber);
	}
}
