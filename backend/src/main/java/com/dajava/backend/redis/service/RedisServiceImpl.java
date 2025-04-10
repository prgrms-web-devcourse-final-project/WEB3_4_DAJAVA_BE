package com.dajava.backend.redis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.service.SessionDataService;
import com.dajava.backend.redis.buffer.EventRedisBuffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {
	private final EventRedisBuffer eventRedisBuffer;
	private final SessionDataService sessionDataService;
	private final SessionDataDocumentRepository sessionDataDocumentRepository;
	private final RedisActivityHandleService redisActivityHandleService;

	@Override
	@Transactional
	public void createClickEvent(PointerClickEventRequest request) {
		log.info("클릭 이벤트 로깅: {}", request);
		SessionDataKey sessionDataKey = new SessionDataKey(
			request.getSessionId(), request.getPageUrl(), request.getMemberSerialNumber()
		);
		eventRedisBuffer.addClickEvent(request, sessionDataKey);
	}

	@Override
	@Transactional
	public void createMoveEvent(PointerMoveEventRequest request) {
		log.info("이동 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.getSessionId(), request.getPageUrl(), request.getMemberSerialNumber()
		);
		eventRedisBuffer.addMoveEvent(request, sessionDataKey);
	}

	@Override
	@Transactional
	public void createScrollEvent(PointerScrollEventRequest request) {
		log.info("스크롤 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.getSessionId(), request.getPageUrl(), request.getMemberSerialNumber()
		);
		eventRedisBuffer.addScrollEvent(request, sessionDataKey);
	}

	@Override
	@Transactional
	public void startSession(SessionDataKey sessionDataKey) {
		log.info("세션 시작");
		SessionDataDocument esData = SessionDataDocument.create(
			sessionDataKey.sessionId(),
			sessionDataKey.memberSerialNumber(),
			sessionDataKey.pageUrl(),
			System.currentTimeMillis()
		);
		sessionDataDocumentRepository.save(esData);
		// 중복이 있으면 그걸 담아 ?
	}

	@Override
	@Transactional
	public void expireSession(String sessionId) {
		log.info("세션 종료");
		SessionDataDocument esData = sessionDataDocumentRepository.findBySessionId(sessionId)
			.orElseThrow();
		SessionDataKey sessionDataKey = new SessionDataKey(
			esData.getSessionId(), esData.getPageUrl(), esData.getMemberSerialNumber()
		);
		redisActivityHandleService.processInactiveBatchForSession(sessionDataKey);
	}

}
