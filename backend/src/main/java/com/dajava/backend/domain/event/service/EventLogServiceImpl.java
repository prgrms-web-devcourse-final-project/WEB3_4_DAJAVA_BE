package com.dajava.backend.domain.event.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.global.component.buffer.EventBuffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EventLogServiceImpl
 * EventLogService 인터페이스 구현체
 * 각 이벤트를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
 *
 * @author NohDongHui, Metronon
 * @since 2025-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventLogServiceImpl implements EventLogService {
	private final SessionDataRepository sessionDataRepository;
	private final SessionDataService sessionDataService;
	private final EventBuffer eventBuffer;
	private final ActivityHandleService activityHandleService;

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

		// 클릭 이벤트 버퍼링
		eventBuffer.addClickEvent(request, sessionDataKey);
	}

	/**
	 * 무브 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 */
	@Override
	@Transactional
	public void createMoveEvent(PointerMoveEventRequest request) {
		log.info("이동 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);

		// SessionData 를 통해 Cache 확인, 없으면 생성
		sessionDataService.createOrFindSessionData(sessionDataKey);

		// 이동 이벤트 버퍼링
		eventBuffer.addMoveEvent(request, sessionDataKey);
	}

	/**
	 * 스크롤 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 */
	@Override
	@Transactional
	public void createScrollEvent(PointerScrollEventRequest request) {
		log.info("스크롤 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);

		// SessionData 를 통해 Cache 확인, 없으면 생성
		sessionDataService.createOrFindSessionData(sessionDataKey);

		// 스크롤 이벤트 버퍼링
		eventBuffer.addScrollEvent(request, sessionDataKey);
	}

	@Override
	@Transactional
	public void expireSession(String sessionId) {
		log.info("세션 종료");

		SessionData data = sessionDataRepository.findBySessionId(sessionId)
			.orElseThrow();

		SessionDataKey sessionDataKey = new SessionDataKey(
			data.getSessionId(), data.getPageUrl(), data.getMemberSerialNumber()
		);

		activityHandleService.processInactiveBatchForSession(sessionDataKey);
	}
}


