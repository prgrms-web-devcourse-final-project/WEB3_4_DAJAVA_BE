package com.dajava.backend.domain.event.es.service;

import org.springframework.stereotype.Service;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * PointerEventDocument 인터페이스 구현체
 * eventBuffer 에 존재하는 캐싱 리스트의 배치 처리를 담당하는 로직입니다.
 * 스케쥴러와 연계되어 비동기적으로 작동합니다.
 * @author NohDongHui
 */
@RequiredArgsConstructor
@Service
public class PointerEventDocumentServiceImpl implements PointerEventDocumentService {

	private final SessionDataDocumentRepository sessionDataDocumentRepository;

	//더티 체킹 안되니 직접 저장해야함
	@Override
	public void endSession(String sessionId) {
		SessionDataDocument doc = sessionDataDocumentRepository.findBySessionId(sessionId).orElseThrow(() ->
		new PointerEventException(ErrorCode.EVENT_DOCUMENT_NOT_FOUND));
		doc.endSession();
		sessionDataDocumentRepository.save(doc); // 전체 저장
	}
}
