package com.dajava.backend.domain.event.es.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;

import lombok.RequiredArgsConstructor;

/**
 * SessionDataDocumentService 인터페이스 구현체
 *
 * @author NohDongHui
 */
@RequiredArgsConstructor
@Service
public class SessionDataDocumentServiceImpl implements SessionDataDocumentService {

	private final SessionDataDocumentRepository sessionDataDocumentRepository;

	@Override
	public Page<SessionDataDocument> getEndedSessions(int page, int size) {
		PageRequest pageRequest = PageRequest.of(page, size);
		return sessionDataDocumentRepository.findByIsSessionEndedTrue(pageRequest);
	}

	@Override
	public void save(SessionDataDocument sessionDataDocument) {
		sessionDataDocumentRepository.save(sessionDataDocument);
	}

}
