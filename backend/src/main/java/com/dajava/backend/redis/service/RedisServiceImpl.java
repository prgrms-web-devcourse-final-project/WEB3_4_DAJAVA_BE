package com.dajava.backend.redis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
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
	private final EventRedisBuffer eventRedisBuffer;
	private final RedisSessionDataService redisSessionDataService;

	@Override
	@Transactional
	public void createClickEvent(PointerClickEventRequest request) {
		log.info("클릭 이벤트 로깅: {}", request);
		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);
		redisSessionDataService.createOrFindSessionDataDocument(sessionDataKey);
		eventRedisBuffer.addClickEvent(request, sessionDataKey);
	}

	@Override
	@Transactional
	public void createMoveEvent(PointerMoveEventRequest request) {
		log.info("이동 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);
		redisSessionDataService.createOrFindSessionDataDocument(sessionDataKey);
		eventRedisBuffer.addMoveEvent(request, sessionDataKey);
	}

	@Override
	@Transactional
	public void createScrollEvent(PointerScrollEventRequest request) {
		log.info("스크롤 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);
		redisSessionDataService.createOrFindSessionDataDocument(sessionDataKey);
		eventRedisBuffer.addScrollEvent(request, sessionDataKey);
	}
}
