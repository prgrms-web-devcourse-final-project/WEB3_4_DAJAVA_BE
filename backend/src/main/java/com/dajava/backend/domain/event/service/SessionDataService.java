package com.dajava.backend.domain.event.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.repository.SessionDataRepository;

import lombok.RequiredArgsConstructor;

/**
 * SessionData 가 현재 Cache 에 존재하는지 확인하고, 없을 시 생성 및 Cache 에 저장하는 로직입니다.
 * computeIfAbsent 로직을 통해 Cache 에 데이터가 존재하지 않을시 생성 및 Cache 에 올려놓습니다.
 *
 * @author Metronon
 */
@Service
@RequiredArgsConstructor
public class SessionDataService {
	private final SessionDataRepository sessionDataRepository;
	private final SessionDataDocumentRepository sessionDataDocumentRepository;

	private final Map<SessionDataKey, SessionData> sessionCache = new ConcurrentHashMap<>();
	private final Map<SessionDataKey, SessionDataDocument> sessionEsCache = new ConcurrentHashMap<>();

	@Transactional
	public SessionData createOrFindSessionData(SessionDataKey key) {
		return sessionCache.computeIfAbsent(key, k ->
			sessionDataRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
					k.pageUrl(), k.sessionId(), k.memberSerialNumber()
				)
				.orElseGet(() -> {
					SessionData newSession = SessionData.builder()
						.sessionId(k.sessionId())
						.pageUrl(k.pageUrl())
						.memberSerialNumber(k.memberSerialNumber())
						.isOutlier(false)
						.isMissingValue(false)
						.isSessionEnded(false)
						.isVerified(false)
						.build();
					return sessionDataRepository.save(newSession);
				})
		);
	}

	// DB에 반영 완료시 Cache 에서 제거하는 로직
	public void removeFromCache(SessionDataKey key) {
		sessionCache.remove(key);
	}

	//session 엔티티 일련번호는 sessionId+url+serialNum으로 한다.
	//트랜잭션이 보장 되지 않기 때문에 중복된 데이터가 들어간 경우 원래 있던 데이터에 덮어쓰기 형태가 되어야함.
	public SessionDataDocument createOrFindSessionDataDocument(SessionDataKey key) {
		return sessionEsCache.computeIfAbsent(key, k ->
			sessionDataDocumentRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
					k.pageUrl(), k.sessionId(), k.memberSerialNumber()
				)
				.orElseGet(() -> {
					SessionDataDocument newSession = SessionDataDocument.builder()
						.id(k.sessionId() + k.pageUrl() + k.memberSerialNumber())
						.sessionId(k.sessionId())
						.pageUrl(k.pageUrl())
						.memberSerialNumber(k.memberSerialNumber())
						.timestamp(System.currentTimeMillis())
						.isOutlier(false)
						.isMissingValue(false)
						.isSessionEnded(false)
						.isVerified(false)
						.build();
					return sessionDataDocumentRepository.save(newSession);
				})
		);
	}

	// DB에 반영 완료시 Cache 에서 제거하는 로직
	public void removeFromEsCache(SessionDataKey key) {
		sessionEsCache.remove(key);
	}

}

