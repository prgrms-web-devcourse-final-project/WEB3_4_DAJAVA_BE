package com.dajava.backend.domain.event.service;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.global.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * 서비스 로직에서 발생하는 event 예외 처리를 담당합니다.
 *
 * @author NohDongHui
 */
@Slf4j
public class EventValidation {

	public static void validateSessionExists(SessionDataDocument sessionDataDocument, String sessionId) {
		if (sessionDataDocument == null) {
			log.warn("SessionDataDocument is null for sessionId: {}", sessionId);
			throw new PointerEventException(ErrorCode.SESSION_DATA_DOCUMENT_NOT_FOUND);
		}
	}
}
