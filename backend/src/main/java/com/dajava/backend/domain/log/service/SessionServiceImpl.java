package com.dajava.backend.domain.log.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService  {
	private final SessionDataDocumentRepository sessionDataDocumentRepository;
	private final RedisActivityHandleService redisActivityHandleService;

	@Override
	@Transactional
	public void expireSession(String sessionId) {
		SessionDataDocument esData = sessionDataDocumentRepository.findBySessionId(sessionId)
			.orElseThrow();
		SessionIdentifier sessionIdentifier = new SessionIdentifier(
			esData.getSessionId(), esData.getPageUrl(), esData.getMemberSerialNumber()
		);
		redisActivityHandleService.processInactiveBatchForSession(sessionIdentifier);
	}
}
