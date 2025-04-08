package com.dajava.backend.redis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.converter.PointerEventConverter;
import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.entity.PointerClickEvent;
import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.PointerClickEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerMoveEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerScrollEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.repository.PointerClickEventRepository;
import com.dajava.backend.domain.event.repository.PointerMoveEventRepository;
import com.dajava.backend.domain.event.repository.PointerScrollEventRepository;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.domain.event.service.SessionDataService;
import com.dajava.backend.redis.buffer.EventRedisBuffer;

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
	private final RedisSessionDataService redisSessionDataService;
	private final PointerClickEventDocumentRepository pointerClickEventDocumentRepository;
	private final PointerMoveEventDocumentRepository pointerMoveEventDocumentRepository;
	private final PointerScrollEventDocumentRepository pointerScrollEventDocumentRepository;
	private final SessionDataDocumentRepository sessionDataDocumentRepository;

	/**
	 * 각 이벤트 타입의 저장 로직을 배치화한 로직입니다.
	 * @param sessionDataKey sessionData 객체 생성 및 캐싱을 위해 주입합니다.
	 */
	@Transactional
	public void processBatchForSession(SessionDataKey sessionDataKey, boolean isInactive) {
		log.info("{} 세션 이벤트 일괄 처리 시작", sessionDataKey);

		int totalPendingEvents = countPendingEvents(sessionDataKey);

		if (totalPendingEvents == 0) {
			return;
		}

		SessionDataDocument sessionDataDocument = redisSessionDataService.createOrFindSessionDataDocument(sessionDataKey);

		processClickEvents(sessionDataKey);
		processMoveEvents(sessionDataKey);
		processScrollEvents(sessionDataKey);

		if (isInactive) {
			redisSessionDataService.removeFromEsCache(sessionDataKey);
			// 세션 종료 flag 값 true 로 변경
			sessionDataDocument.endSession();
		}
		sessionDataDocumentRepository.save(sessionDataDocument);
	}

	/**
	 *
	 * @param sessionDataKey sessionDataKey 를 통해 버퍼의 이벤트 갯수를 가져옵니다
	 * @return 총 이벤트 갯수 (int)
	 */
	private int countPendingEvents(SessionDataKey sessionDataKey) {
		return eventRedisBuffer.getClickEvents(sessionDataKey).size()
			+ eventRedisBuffer.getMoveEvents(sessionDataKey).size()
			+ eventRedisBuffer.getScrollEvents(sessionDataKey).size();
	}

	/**
	 * 클릭 이벤트의 버퍼에 접근 후, sessionData 에 데이터를 저장합니다.
	 * 현재 es에도 같이 저장합니다.
	 * @param sessionDataKey sessionDataKey 를 통해 eventBuffer 에 접근한 뒤, 관련 이벤트 리스트를 가져오고, 버퍼를 초기화합니다.
	 */
	private void processClickEvents(SessionDataKey sessionDataKey) {
		List<PointerClickEventRequest> clickEvents = eventRedisBuffer.flushClickEvents(sessionDataKey);
		log.info("세션 {}: 클릭 이벤트 {} 개 처리", sessionDataKey, clickEvents.size());

		//es에 저장할 형태
		List<PointerClickEventDocument> documents = new ArrayList<>();
		for (PointerClickEventRequest request : clickEvents) {

			PointerClickEventDocument doc = PointerEventConverter.toClickEventDocument(request);
			documents.add(doc);
		}

		if (!documents.isEmpty()) {
			pointerClickEventDocumentRepository.saveAll(documents); // Elasticsearch 저장
		}
	}

	/**
	 * 무브 이벤트의 버퍼에 접근 후, sessionData 에 데이터를 저장합니다.
	 * @param sessionDataKey sessionDataKey 를 통해 eventBuffer 에 접근한 뒤, 관련 이벤트 리스트를 가져오고, 버퍼를 초기화합니다.
	 */
	private void processMoveEvents(SessionDataKey sessionDataKey) {
		List<PointerMoveEventRequest> moveEvents = eventRedisBuffer.flushMoveEvents(sessionDataKey);
		log.info("세션 {}: 이동 이벤트 {} 개 처리", sessionDataKey, moveEvents.size());

		//es에 저장할 형태
		List<PointerMoveEventDocument> documents = new ArrayList<>();
		for (PointerMoveEventRequest request : moveEvents) {
			PointerMoveEventDocument doc = PointerEventConverter.toMoveEventDocument(request);
			documents.add(doc);
		}

		if (!documents.isEmpty()) {
			pointerMoveEventDocumentRepository.saveAll(documents);
		}
	}

	/**
	 * 스크롤 이벤트의 버퍼에 접근 후, sessionData 에 데이터를 저장합니다.
	 * @param sessionDataKey sessionDataKey 를 통해 eventBuffer 에 접근한 뒤, 관련 이벤트 리스트를 가져오고, 버퍼를 초기화합니다.
	 */
	private void processScrollEvents(SessionDataKey sessionDataKey) {
		List<PointerScrollEventRequest> scrollEvents = eventRedisBuffer.flushScrollEvents(sessionDataKey);
		log.info("세션 {}: 스크롤 이벤트 {} 개 처리", sessionDataKey, scrollEvents.size());

		//es에 저장할 형태
		List<PointerScrollEventDocument> documents = new ArrayList<>();
		for (PointerScrollEventRequest request : scrollEvents) {

			PointerScrollEventDocument doc = PointerEventConverter.toScrollEventDocument(request);
			documents.add(doc);
		}

		if (!documents.isEmpty()) {
			pointerScrollEventDocumentRepository.saveAll(documents);
		}
	}
}

