package com.dajava.backend.domain.log.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;
import com.dajava.backend.global.utils.event.EventRedisBuffer;

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

		SessionIdentifier sessionIdentifier = new SessionIdentifier(
			clickEventRequest.getSessionIdentifier().getSessionId(),
			clickEventRequest.getSessionIdentifier().getPageUrl(),
			clickEventRequest.getSessionIdentifier().getMemberSerialNumber()
		);

		eventRedisBuffer.addClickEvent(clickEventRequest, sessionIdentifier);
	}

	@Override
	@Transactional
	public void createMoveEvent(MovementEventRequest movementEventRequest) {
		log.info("이동 이벤트 로깅: {}", movementEventRequest);

		SessionIdentifier sessionIdentifier = new SessionIdentifier(
			movementEventRequest.getSessionIdentifier().getSessionId(),
			movementEventRequest.getSessionIdentifier().getPageUrl(),
			movementEventRequest.getSessionIdentifier().getMemberSerialNumber()
		);
		eventRedisBuffer.addMoveEvent(movementEventRequest, sessionIdentifier);
	}

	@Override
	@Transactional
	public void createScrollEvent(ScrollEventRequest scrollEventRequest) {
		log.info("스크롤 이벤트 로깅: {}", scrollEventRequest);

		SessionIdentifier sessionIdentifier = new SessionIdentifier(
			scrollEventRequest.getSessionIdentifier().getSessionId(),
			scrollEventRequest.getSessionIdentifier().getPageUrl(),
			scrollEventRequest.getSessionIdentifier().getMemberSerialNumber()
		);
		eventRedisBuffer.addScrollEvent(scrollEventRequest, sessionIdentifier);
	}

}
