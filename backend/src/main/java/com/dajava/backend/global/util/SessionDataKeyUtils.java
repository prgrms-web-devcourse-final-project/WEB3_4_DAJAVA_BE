package com.dajava.backend.global.util;

import com.dajava.backend.domain.event.dto.SessionDataKey;

/**
 * key와 SessionDataKey DTO간의 변환 유틸입니다.
 * @author Metronon, nodonghui
 */
public final class SessionDataKeyUtils {
	private static final String DELIMITER = "|";

	private SessionDataKeyUtils() {
	}

	/**
	 * SessionDataKey 객체를 구분자(|)를 이용한 문자열로 변환합니다.
	 *
	 * @param sessionDataKey SessionDataKey 객체 (null이면 안 됨)
	 * @return 변환된 문자열 (예: "sessionId|pageUrl|memberSerialNumber")
	 * @throws IllegalArgumentException sessionDataKey 가 null 인 경우
	 */
	public static String toKey(SessionDataKey sessionDataKey) {
		if (sessionDataKey == null) {
			throw new IllegalArgumentException("sessionDataKey 는 null 이거나 비어있을 수 없습니다.");
		}
		return sessionDataKey.sessionId() + DELIMITER
			+ sessionDataKey.pageUrl() + DELIMITER
			+ sessionDataKey.memberSerialNumber();
	}

	/**
	 * 구분자(|)로 결합된 문자열을 SessionDataKey 객체로 변환합니다.
	 *
	 * @param key 변환할 문자열
	 * @return 변환된 SessionDataKey 객체
	 * @throws IllegalArgumentException key 가 null 이거나 형식이 올바르지 않은 경우
	 */
	public static SessionDataKey parseKey(String key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Key 문자열은 null 이거나 비어있을 수 없습니다");
		}

		String[] parts = key.split("\\|");
		if (parts.length != 3) {
			throw new IllegalArgumentException("올바르지 않은 키 형식: " + key);
		}

		String sessionId = parts[0];
		String pageUrl = parts[1];
		String memberSerialNumber = parts[2];

		return new SessionDataKey(sessionId, pageUrl, memberSerialNumber);
	}
}
