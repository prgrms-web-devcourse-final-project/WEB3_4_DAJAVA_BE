package com.dajava.backend.redis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.domain.event.service.ActivityHandleService;
import com.dajava.backend.domain.event.service.SessionDataService;
import com.dajava.backend.redis.buffer.EventRedisBuffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {
	private final SessionDataRepository sessionDataRepository;
	private final SessionDataService sessionDataService;
	private final EventRedisBuffer eventRedisBuffer;
	private final ActivityHandleService activityHandleService;
	private final SessionDataDocumentRepository sessionDataDocumentRepository;
	/**
	 * 클릭 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 */
	@Override
	@Transactional
	public void createClickEvent(PointerClickEventRequest request) {
		log.info("클릭 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);

		// SessionData 를 통해 Cache 확인, 없으면 생성
		sessionDataService.createOrFindSessionData(sessionDataKey);
		// es 용
		sessionDataService.createOrFindSessionDataDocument(sessionDataKey);
		// 클릭 이벤트 버퍼링
		eventRedisBuffer.addClickEvent(request, sessionDataKey);
	}


}
