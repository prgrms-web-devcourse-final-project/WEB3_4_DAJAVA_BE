package com.dajava.backend.domain.log.service;

import org.springframework.stereotype.Service;

import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;
import com.dajava.backend.domain.log.handler.EventHandler;
import com.dajava.backend.domain.log.handler.SessionIdentifierExtractor;
import com.dajava.backend.global.utils.event.EventRedisBuffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
	private final EventRedisBuffer eventRedisBuffer;

	@Override
	public void createClickEvent(ClickEventRequest request) {
		handleEvent("클릭 이벤트", request,
			req -> toSessionIdentifier(req.getSessionIdentifier()),
			eventRedisBuffer::addClickEvent
		);
	}
	@Override
	public void createMoveEvent(MovementEventRequest request) {
		handleEvent("이동 이벤트", request,
			req -> toSessionIdentifier(req.getSessionIdentifier()),
			eventRedisBuffer::addMoveEvent
		);
	}
	@Override
	public void createScrollEvent(ScrollEventRequest request) {
		handleEvent("스크롤 이벤트", request,
			req -> toSessionIdentifier(req.getSessionIdentifier()),
			eventRedisBuffer::addScrollEvent
		);
	}

	private <T> void handleEvent(
		String logLabel,
		T request,
		SessionIdentifierExtractor<T> extractor,
		EventHandler<T> handler
	) {
		try {
			SessionIdentifier sessionIdentifier = extractor.extract(request);
			log.info("[{}] sessionId={}, pageUrl={}, memberSerial={}", logLabel,
				sessionIdentifier.getSessionId(),
				sessionIdentifier.getPageUrl(),
				sessionIdentifier.getMemberSerialNumber()
			);
			handler.handle(request, sessionIdentifier);
		} catch (Exception e) {
			log.error("[{}][에러] 이벤트 실패: {}", logLabel, request, e);
			throw e;
		}
	}

	private SessionIdentifier toSessionIdentifier(SessionIdentifier sessionIdentifier) {
		return new SessionIdentifier(sessionIdentifier.getSessionId(), sessionIdentifier.getPageUrl(), sessionIdentifier.getMemberSerialNumber());
	}
}
