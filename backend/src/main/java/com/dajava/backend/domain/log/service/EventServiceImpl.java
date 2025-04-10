package com.dajava.backend.domain.log.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;
import com.dajava.backend.global.utils.EventRedisBuffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
	private final EventRedisBuffer eventRedisBuffer;


	@Override
	@Transactional
	public void createClickEvent(ClickEventRequest clickEventRequest) {
		log.info("클릭 이벤트 로깅: {}", clickEventRequest);
		SessionIdentifier sessionIdentifier = new SessionIdentifier();
		eventRedisBuffer.addClickEvent(clickEventRequest, sessionIdentifier);
	}

	@Override
	@Transactional
	public void createMoveEvent(PointerMoveEventRequest request) {
		log.info("이동 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);
		eventRedisBuffer.addMoveEvent(request, sessionDataKey);
	}

	@Override
	@Transactional
	public void createScrollEvent(PointerScrollEventRequest request) {
		log.info("스크롤 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);
		eventRedisBuffer.addScrollEvent(request, sessionDataKey);
	}

}
