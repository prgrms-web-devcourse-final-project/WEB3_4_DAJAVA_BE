package com.dajava.backend.domain.log.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.PointerClickEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerMoveEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerScrollEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.log.converter.EventConverter;
import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;
import com.dajava.backend.global.utils.event.EventRedisBuffer;

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
public class RedisEventBatchService {
	private final EventRedisBuffer eventRedisBuffer;

	private final SessionDataDocumentRepository sessionDataDocumentRepository;
	private final RedisSessionDataService redisSessionDataService;
	private final PointerClickEventDocumentRepository pointerClickEventDocumentRepository;
	private final PointerMoveEventDocumentRepository pointerMoveEventDocumentRepository;
	private final PointerScrollEventDocumentRepository pointerScrollEventDocumentRepository;

	/**
	 * 각 이벤트 타입의 저장 로직을 배치화한 로직입니다.
	 * @param sessionIdentifier sessionData 객체 생성 및 캐싱을 위해 주입합니다.
	 */
	@Transactional
	public void processBatchForSession(SessionIdentifier sessionIdentifier, boolean isInactive) {
		log.info("{} 세션 이벤트 일괄 처리 시작", sessionIdentifier);

		int totalPendingEvents = countPendingEvents(sessionIdentifier);

		if (totalPendingEvents == 0) {
			return;
		}

		SessionDataDocument sessionDataDocument = redisSessionDataService.createOrFindSessionDataDocument(sessionIdentifier);

		processClickEvents(sessionIdentifier);
		processMoveEvents(sessionIdentifier);
		processScrollEvents(sessionIdentifier);

		if (isInactive) {
			redisSessionDataService.removeFromEsCache(sessionIdentifier);
			// 세션 종료 flag 값 true 로 변경
			sessionDataDocument.endSession();
		}
		sessionDataDocumentRepository.save(sessionDataDocument);
	}

	/**
	 *
	 * @param sessionIdentifier sessionDataKey 를 통해 버퍼의 이벤트 갯수를 가져옵니다
	 * @return 총 이벤트 갯수 (int)
	 */
	private int countPendingEvents(SessionIdentifier sessionIdentifier) {
		return eventRedisBuffer.getClickEvents(sessionIdentifier).size()
			+ eventRedisBuffer.getMoveEvents(sessionIdentifier).size()
			+ eventRedisBuffer.getScrollEvents(sessionIdentifier).size();
	}

	/**
	 * 클릭 이벤트의 버퍼에 접근 후, sessionData 에 데이터를 저장합니다.
	 * 현재 es에도 같이 저장합니다.
	 * @param sessionIdentifier sessionDataKey 를 통해 eventBuffer 에 접근한 뒤, 관련 이벤트 리스트를 가져오고, 버퍼를 초기화합니다.
	 */
	private void processClickEvents(SessionIdentifier sessionIdentifier) {
		List<ClickEventRequest> clickEvents = eventRedisBuffer.flushClickEvents(sessionIdentifier);
		log.info("세션 {}: 클릭 이벤트 {} 개 처리", sessionIdentifier, clickEvents.size());
		List<PointerClickEventDocument> documents = new ArrayList<>();
		for (ClickEventRequest request : clickEvents) {

			PointerClickEventDocument doc = EventConverter.toClickEventDocument(request);
			documents.add(doc);
		}

		if (!documents.isEmpty()) {
			pointerClickEventDocumentRepository.saveAll(documents);
		}
	}

	/**
	 * 무브 이벤트의 버퍼에 접근 후, sessionData 에 데이터를 저장합니다.
	 * @param sessionIdentifier sessionDataKey 를 통해 eventBuffer 에 접근한 뒤, 관련 이벤트 리스트를 가져오고, 버퍼를 초기화합니다.
	 */
	private void processMoveEvents(SessionIdentifier sessionIdentifier) {
		List<MovementEventRequest> moveEvents = eventRedisBuffer.flushMoveEvents(sessionIdentifier);
		log.info("세션 {}: 이동 이벤트 {} 개 처리", sessionIdentifier, moveEvents.size());
		List<PointerMoveEventDocument> documents = new ArrayList<>();
		for (MovementEventRequest request : moveEvents) {
			PointerMoveEventDocument doc = EventConverter.toMoveEventDocument(request);
			documents.add(doc);
		}

		if (!documents.isEmpty()) {
			pointerMoveEventDocumentRepository.saveAll(documents);
		}
	}

	/**
	 * 스크롤 이벤트의 버퍼에 접근 후, sessionData 에 데이터를 저장합니다.
	 * @param sessionIdentifier sessionDataKey 를 통해 eventBuffer 에 접근한 뒤, 관련 이벤트 리스트를 가져오고, 버퍼를 초기화합니다.
	 */
	private void processScrollEvents(SessionIdentifier sessionIdentifier) {
		List<ScrollEventRequest> scrollEvents = eventRedisBuffer.flushScrollEvents(sessionIdentifier);
		log.info("세션 {}: 스크롤 이벤트 {} 개 처리", sessionIdentifier, scrollEvents.size());
		List<PointerScrollEventDocument> documents = new ArrayList<>();
		for (ScrollEventRequest request : scrollEvents) {

			PointerScrollEventDocument doc = EventConverter.toScrollEventDocument(request);
			documents.add(doc);
		}
		if (!documents.isEmpty()) {
			pointerScrollEventDocumentRepository.saveAll(documents);
		}
	}
}

