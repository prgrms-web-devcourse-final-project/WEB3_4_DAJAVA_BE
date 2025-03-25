package com.dajava.backend.domain.event.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.SessionData;
import com.dajava.backend.domain.event.dto.SessionDataKey;
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

	private final Map<SessionDataKey, SessionData> sessionCache = new ConcurrentHashMap<>();

	@Transactional
	public SessionData createOrFindSessionData(SessionDataKey key) {
		return sessionCache.computeIfAbsent(key, k ->
			sessionDataRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
					k.sessionId(), k.pageUrl(), k.memberSerialNumber()
				)
				.orElseGet(() -> {
					SessionData newSession = SessionData.builder()
						.sessionId(k.sessionId())
						.pageUrl(k.pageUrl())
						.memberSerialNumber(k.memberSerialNumber())
						.isOutlier(false)
						.isMissingValue(false)
						.isVerified(false)
						.build();
					return sessionDataRepository.save(newSession);
				})
		);
	}
}


