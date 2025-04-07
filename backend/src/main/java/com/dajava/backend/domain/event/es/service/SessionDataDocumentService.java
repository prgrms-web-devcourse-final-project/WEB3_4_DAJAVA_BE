package com.dajava.backend.domain.event.es.service;

import org.springframework.data.domain.Page;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;

/**
 *
 * SessionDataDocumen 도메인의 서비스 로직을 처리하는 인터페이스
 *
 * @author NohDongHui
 */
public interface SessionDataDocumentService {

	/**
	 * 세션이 끝난 데이터를 페이징으로 분할해 반환
	 *  스케줄러에서 for문으로 분할해 가져옴
	 * @param page, pageSize
	 * @return SessionDataDocuments 일부를 가진 페이징 객체
	 */
	public Page<SessionDataDocument> getEndedSessions(int page, int size);
}
