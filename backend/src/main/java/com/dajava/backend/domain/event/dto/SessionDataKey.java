package com.dajava.backend.domain.event.dto;

/**
 * 이벤트가 들어올시 SessionDataCache 에 데이터가 존재하는지 확인하기 위한 Key 입니다.
 */
public record SessionDataKey(
	String sessionId,
	String pageUrl,
	String memberSerialNumber
) {
}
