package com.dajava.backend.domain.log.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService  {
	private final SessionDataDocumentRepository sessionDataDocumentRepository;
	private final RedisActivityHandleService redisActivityHandleService;

	@Override
	@Transactional
	public void startSession(SessionDataKey sessionDataKey) {
		SessionDataDocument esData = SessionDataDocument.create(
			sessionDataKey.sessionId(),
			sessionDataKey.memberSerialNumber(),
			sessionDataKey.pageUrl(),
			System.currentTimeMillis()
		);
		sessionDataDocumentRepository.save(esData);
	}

	@Override
	@Transactional
	public void expireSession(String sessionId) {
		SessionDataDocument esData = sessionDataDocumentRepository.findBySessionId(sessionId)
			.orElseThrow();
		SessionDataKey sessionDataKey = new SessionDataKey(
			esData.getSessionId(), esData.getPageUrl(), esData.getMemberSerialNumber()
		);
		redisActivityHandleService.processInactiveBatchForSession(sessionDataKey);
	}
}
