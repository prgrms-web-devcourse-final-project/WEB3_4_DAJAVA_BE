package com.dajava.backend.domain.log.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SessionData 가 현재 Cache 에 존재하는지 확인하고, 없을 시 생성 및 Cache 에 저장하는 로직입니다.
 * computeIfAbsent 로직을 통해 Cache 에 데이터가 존재하지 않을시 생성 및 Cache 에 올려놓습니다.
 *
 * @author Metronon
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSessionDataService {
	private final SessionDataDocumentRepository sessionDataDocumentRepository;

	private final Map<SessionIdentifier, SessionDataDocument> sessionEsCache = new ConcurrentHashMap<>();


	public SessionDataDocument createOrFindSessionDataDocument(SessionIdentifier sessionIdentifier) {
		return sessionEsCache.computeIfAbsent(sessionIdentifier, k ->
			// k 가 있으면
			sessionDataDocumentRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
					k.getPageUrl(), sessionIdentifier.getSessionId(), k.getMemberSerialNumber()
				)
				// 없으면
				.orElseGet(() -> {
					SessionDataDocument newSession = SessionDataDocument.create(
						k.getSessionId(),
						k.getMemberSerialNumber(),
						k.getPageUrl(),
						System.currentTimeMillis()
					);
					return sessionDataDocumentRepository.save(newSession);
				})
		);
	}

	// DB에 반영 완료시 Cache 에서 제거하는 로직
	public void removeFromEsCache(SessionIdentifier sessionIdentifier) {
		sessionEsCache.remove(sessionIdentifier);
	}
}