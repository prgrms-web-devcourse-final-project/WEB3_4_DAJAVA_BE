package com.dajava.backend.domain.event.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.PointerClickEvent;
import com.dajava.backend.domain.event.PointerMoveEvent;
import com.dajava.backend.domain.event.PointerScrollEvent;
import com.dajava.backend.domain.event.SessionData;
import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.repository.PointerClickEventRepository;
import com.dajava.backend.domain.event.repository.PointerMoveEventRepository;
import com.dajava.backend.domain.event.repository.PointerScrollEventRepository;
import com.dajava.backend.domain.event.repository.SessionDataRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EventLogServiceImpl
 * EventLogService 인터페이스 구현체
 *
 * @author NohDongHui, Metronon
 * @since 2025-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventLogServiceImpl implements EventLogService {

	private final SessionDataRepository sessionDataRepository;
	private final PointerClickEventRepository clickEventRepository;
	private final PointerMoveEventRepository moveEventRepository;
	private final PointerScrollEventRepository scrollEventRepository;
	private final SessionDataService sessionDataService;

	/**
	 *
	 * @param request
	 */
	@Override
	@Transactional
	public void createClickEvent(PointerClickEventRequest request) {
		log.info("클릭 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);

		// SessionData 를 통해 Cache 확인, 없으면 생성
		SessionData sessionData = sessionDataService.createOrFindSessionData(
			sessionDataKey
		);

		// PointerClickEvent 생성
		PointerClickEvent clickEvent = PointerClickEvent.create(
			request.clientX(),
			request.clientY(),
			request.pageUrl(),
			request.browserWidth(),
			request.sessionId(),
			request.memberSerialNumber(),
			sessionData
		);

		// TODO : 각 이벤트 저장이 아닌 버퍼링 후 배치 처리
		// clickEventRepository.save(clickEvent);
	}

	@Override
	@Transactional
	public void createMoveEvent(PointerMoveEventRequest request) {
		log.info("이동 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);

		// SessionData 를 통해 Cache 확인, 없으면 생성
		SessionData sessionData = sessionDataService.createOrFindSessionData(
			sessionDataKey
		);

		// PointerMoveEvent 생성
		PointerMoveEvent moveEvent = PointerMoveEvent.create(
			request.clientX(),
			request.clientY(),
			request.pageUrl(),
			request.browserWidth(),
			request.sessionId(),
			request.memberSerialNumber(),
			sessionData
		);

		// TODO : 각 이벤트 저장이 아닌 버퍼링 후 배치 처리
		// moveEventRepository.save(moveEvent);
	}

	@Override
	@Transactional
	public void createScrollEvent(PointerScrollEventRequest request) {
		log.info("스크롤 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);

		// SessionData 를 통해 Cache 확인, 없으면 생성
		SessionData sessionData = sessionDataService.createOrFindSessionData(
			sessionDataKey
		);

		// PointerScrollEvent 생성
		PointerScrollEvent scrollEvent = PointerScrollEvent.create(
			request.scrollY(),
			request.pageUrl(),
			request.browserWidth(),
			request.sessionId(),
			request.memberSerialNumber(),
			sessionData
		);

		// TODO : 각 이벤트 저장이 아닌 버퍼링 후 배치 처리
		// scrollEventRepository.save(scrollEvent);
	}
}

