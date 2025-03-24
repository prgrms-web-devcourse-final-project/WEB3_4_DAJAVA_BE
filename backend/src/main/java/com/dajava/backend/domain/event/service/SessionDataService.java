package com.dajava.backend.domain.event.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.SessionData;
import com.dajava.backend.domain.event.repository.SessionDataRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionDataService {
	private final SessionDataRepository sessionDataRepository;

	private final Map<String, SessionData> sessionCache = new ConcurrentHashMap<>();

	@Transactional
	public SessionData createOrFindSessionData(String pageUrl, String sessionId, String memberSerialNumber) {
		SessionData sessionData = sessionCache.get(pageUrl + sessionId + memberSerialNumber);

		if (sessionData == null) {
			sessionData = sessionDataRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(pageUrl, sessionId,
					memberSerialNumber)
				.orElseGet(() -> {
					SessionData newSession = SessionData.builder()
						.pageUrl(pageUrl)
						.sessionId(sessionId)
						.memberSerialNumber(memberSerialNumber)
						.isOutlier(false)
						.isMissingValue(false)
						.isVerified(false)
						.build();
					return sessionDataRepository.save(newSession);
				});
			sessionCache.put(pageUrl + sessionId + memberSerialNumber, sessionData);
		}
		return sessionData;
	}
}
