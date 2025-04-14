package com.dajava.backend.domain.log.service;

import org.springframework.stereotype.Service;

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
	private final RedisSessionDataService redisSessionDataService;

	@Override
	public void createClickEvent(ClickEventRequest request) {
		try {
			SessionIdentifier sessionIdentifier = new SessionIdentifier(
				request.getSessionIdentifier().getSessionId(),
				request.getSessionIdentifier().getPageUrl(),
				request.getSessionIdentifier().getMemberSerialNumber()
			);
			// log.info("[클릭 이벤트] sessionId={}, pageUrl={}, memberSerial={}",
			// 	sessionIdentifier.getSessionId(),
			// 	sessionIdentifier.getPageUrl(),
			// 	sessionIdentifier.getMemberSerialNumber()
			// );
			redisSessionDataService.createOrFindSessionDataDocument(sessionIdentifier);
			eventRedisBuffer.addClickEvent(request, sessionIdentifier);
		} catch (Exception e) {
			log.error("[클릭 이벤트][에러] 클릭 이벤트 실패: {}", request, e);
			throw e;
		}
	}

	@Override
	public void createMoveEvent(MovementEventRequest request) {
		try {
			SessionIdentifier sessionIdentifier = new SessionIdentifier(
				request.getSessionIdentifier().getSessionId(),
				request.getSessionIdentifier().getPageUrl(),
				request.getSessionIdentifier().getMemberSerialNumber()
			);
			// log.info("[이동 이벤트] sessionId={}, pageUrl={}, memberSerial={}",
			// 	sessionIdentifier.getSessionId(),
			// 	sessionIdentifier.getPageUrl(),
			// 	sessionIdentifier.getMemberSerialNumber()
			// );
			redisSessionDataService.createOrFindSessionDataDocument(sessionIdentifier);

			eventRedisBuffer.addMoveEvent(request, sessionIdentifier);
		} catch (Exception e) {
			log.error("[이동 이벤트][에러] 이동 이벤트 실패: {}", request, e);
			throw e;
		}
	}

	@Override
	public void createScrollEvent(ScrollEventRequest request) {
		try {
			SessionIdentifier sessionIdentifier = new SessionIdentifier(
				request.getSessionIdentifier().getSessionId(),
				request.getSessionIdentifier().getPageUrl(),
				request.getSessionIdentifier().getMemberSerialNumber()
			);

			// log.info("[스크롤 이벤트] sessionId={}, pageUrl={}, memberSerial={}",
			// 	sessionIdentifier.getSessionId(),
			// 	sessionIdentifier.getPageUrl(),
			// 	sessionIdentifier.getMemberSerialNumber()
			// );
			redisSessionDataService.createOrFindSessionDataDocument(sessionIdentifier);
			eventRedisBuffer.addScrollEvent(request, sessionIdentifier);
		} catch (Exception e) {
			log.error("[스크롤 이벤트][에러] 스크롤 이벤트 실패: {}", request, e);
			throw e;
		}
	}
}
