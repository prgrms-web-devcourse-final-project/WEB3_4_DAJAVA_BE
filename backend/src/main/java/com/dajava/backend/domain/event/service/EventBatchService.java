package com.dajava.backend.domain.event.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.dajava.backend.global.component.buffer.EventBuffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * eventBuffer 에 존재하는 캐싱 리스트의 배치 처리를 담당하는 로직입니다.
 * 스케쥴러와 연계되어 비동기적으로 작동합니다.
 * @author Metronon
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventBatchService {
	private final EventBuffer eventBuffer;
	private final SessionDataService sessionDataService;
	private final PointerClickEventRepository clickRepository;
	private final PointerMoveEventRepository moveRepository;
	private final PointerScrollEventRepository scrollRepository;
	private final SessionDataRepository sessionDataRepository;

	/**
	 * 각 이벤트 타입의 저장 로직을 배치화한 로직입니다.
	 * @param sessionDataKey sessionData 객체 생성 및 캐싱을 위해 주입합니다.
	 */
	@Transactional
	public void processBatchForSession(SessionDataKey sessionDataKey) {
		log.info("{} 세션 이벤트 일괄 처리 시작", sessionDataKey);

		int totalPendingEvents = countPendingEvents(sessionDataKey);

		if (totalPendingEvents == 0) {
			return;
		}

		SessionData sessionData = sessionDataService.createOrFindSessionData(sessionDataKey);

		processClickEvents(sessionDataKey, sessionData);
		processMoveEvents(sessionDataKey, sessionData);
		processScrollEvents(sessionDataKey, sessionData);

		sessionDataRepository.save(sessionData);
		sessionDataService.removeFromCache(sessionDataKey);
	}

	/**
	 * 배치 처리 로직을 스케쥴러와 연결하기 위한 메서드입니다.
	 */
	@Transactional
	public void processAllPendingEvents() {
		Set<SessionDataKey> activeSessionKeys = collectActiveSessionKeys();
		log.info("일괄 처리할 활성 세션 수: {}", activeSessionKeys.size());

		for (SessionDataKey key : activeSessionKeys) {
			try {
				processBatchForSession(key);
			} catch (Exception e) {
				log.error("세션 {} 처리 중 오류 발생: {}", key, e.getMessage(), e);
			}
		}
	}

	/**
	 * eventBuffer 에서 활성 상태인 Session Set 을 가져오기 위한 메서드입니다.
	 * @return Set 현재 활성 상태인 Session 의 키 Set 입니다.
	 */
	public Set<SessionDataKey> collectActiveSessionKeys() {
		return new HashSet<>(eventBuffer.getAllActiveSessionKeys());
	}

	/**
	 *
	 * @param sessionDataKey sessionDataKey 를 통해 버퍼의 이벤트 갯수를 가져옵니다
	 * @return 총 이벤트 갯수 (int)
	 */
	private int countPendingEvents(SessionDataKey sessionDataKey) {
		return eventBuffer.getClickEvents(sessionDataKey).size()
			+ eventBuffer.getMoveEvents(sessionDataKey).size()
			+ eventBuffer.getScrollEvents(sessionDataKey).size();
	}

	/**
	 * 클릭 이벤트의 버퍼에 접근 후, sessionData 에 데이터를 저장합니다.
	 * @param sessionDataKey sessionDataKey 를 통해 eventBuffer 에 접근한 뒤, 관련 이벤트 리스트를 가져오고, 버퍼를 초기화합니다.
	 * @param sessionData sessionData 를 통해 클릭 이벤트 전체를 리스트화 해 저장합니다.
	 */
	private void processClickEvents(SessionDataKey sessionDataKey, SessionData sessionData) {
		List<PointerClickEventRequest> clickEvents = eventBuffer.flushClickEvents(sessionDataKey);
		log.info("세션 {}: 클릭 이벤트 {} 개 처리", sessionDataKey, clickEvents.size());

		List<PointerClickEvent> entities = new ArrayList<>();
		for (PointerClickEventRequest request : clickEvents) {
			PointerClickEvent event = PointerClickEvent.create(
				request.clientX(),
				request.clientY(),
				request.pageUrl(),
				request.browserWidth(),
				request.sessionId(),
				request.memberSerialNumber(),
				sessionData
			);
			entities.add(event);
		}

		if (!entities.isEmpty()) {
			clickRepository.saveAll(entities);
		}
	}

	/**
	 * 무브 이벤트의 버퍼에 접근 후, sessionData 에 데이터를 저장합니다.
	 * @param sessionDataKey sessionDataKey 를 통해 eventBuffer 에 접근한 뒤, 관련 이벤트 리스트를 가져오고, 버퍼를 초기화합니다.
	 * @param sessionData sessionData 를 통해 클릭 이벤트 전체를 리스트화 해 저장합니다.
	 */
	private void processMoveEvents(SessionDataKey sessionDataKey, SessionData sessionData) {
		List<PointerMoveEventRequest> moveEvents = eventBuffer.flushMoveEvents(sessionDataKey);
		log.info("세션 {}: 이동 이벤트 {} 개 처리", sessionDataKey, moveEvents.size());

		List<PointerMoveEvent> entities = new ArrayList<>();
		for (PointerMoveEventRequest request : moveEvents) {
			PointerMoveEvent event = PointerMoveEvent.create(
				request.clientX(),
				request.clientY(),
				request.pageUrl(),
				request.browserWidth(),
				request.sessionId(),
				request.memberSerialNumber(),
				sessionData
			);
			entities.add(event);
		}

		if (!entities.isEmpty()) {
			moveRepository.saveAll(entities);
		}
	}

	/**
	 * 스크롤 이벤트의 버퍼에 접근 후, sessionData 에 데이터를 저장합니다.
	 * @param sessionDataKey sessionDataKey 를 통해 eventBuffer 에 접근한 뒤, 관련 이벤트 리스트를 가져오고, 버퍼를 초기화합니다.
	 * @param sessionData sessionData 를 통해 클릭 이벤트 전체를 리스트화 해 저장합니다.
	 */
	private void processScrollEvents(SessionDataKey sessionDataKey, SessionData sessionData) {
		List<PointerScrollEventRequest> scrollEvents = eventBuffer.flushScrollEvents(sessionDataKey);
		log.info("세션 {}: 스크롤 이벤트 {} 개 처리", sessionDataKey, scrollEvents.size());

		List<PointerScrollEvent> entities = new ArrayList<>();
		for (PointerScrollEventRequest request : scrollEvents) {
			PointerScrollEvent event = PointerScrollEvent.create(
				request.scrollY(),
				request.pageUrl(),
				request.browserWidth(),
				request.sessionId(),
				request.memberSerialNumber(),
				sessionData
			);
			entities.add(event);
		}

		if (!entities.isEmpty()) {
			scrollRepository.saveAll(entities);
		}
	}
}

