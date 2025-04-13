package com.dajava.backend.domain.event.es.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.PointerClickEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerMoveEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerScrollEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PointerEventDocument 인터페이스 구현체
 * eventBuffer 에 존재하는 캐싱 리스트의 배치 처리를 담당하는 로직입니다.
 * 스케쥴러와 연계되어 비동기적으로 작동합니다.
 * @author NohDongHui
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PointerEventDocumentServiceImpl implements PointerEventDocumentService {

	private final PointerClickEventDocumentRepository clickEventDocumentRepository;
	private final PointerMoveEventDocumentRepository moveEventDocumentRepository;
	private final PointerScrollEventDocumentRepository scrollEventDocumentRepository;


	@Override
	public List<PointerClickEventDocument> fetchAllClickEventDocumentsBySessionId(String sessionId,int batchSize) {
		List<PointerClickEventDocument> allEvents = new ArrayList<>();
		int page = 0;
		Page<PointerClickEventDocument> resultPage;

		if (!clickEventDocumentRepository.existsBySessionId(sessionId)) {
			log.info("해당 sessionId에 대한 이벤트 데이터가 없습니다: {}", sessionId);
			return Collections.emptyList();
		}

		do {
			PageRequest pageRequest = PageRequest.of(page, batchSize, Sort.by(Sort.Direction.ASC, "timestamp"));
			resultPage = clickEventDocumentRepository.findBySessionId(sessionId, pageRequest);
			allEvents.addAll(resultPage.getContent());
			page++;
		} while (!resultPage.isLast());

		return allEvents;
	}

	@Override
	public List<PointerMoveEventDocument> fetchAllMoveEventDocumentsBySessionId(String sessionId, int batchSize) {
		List<PointerMoveEventDocument> allEvents = new ArrayList<>();
		int page = 0;
		Page<PointerMoveEventDocument> resultPage;

		if (!moveEventDocumentRepository.existsBySessionId(sessionId)) {
			log.info("해당 sessionId에 대한 이벤트 데이터가 없습니다: {}", sessionId);
			return Collections.emptyList();
		}

		do {
			PageRequest pageRequest = PageRequest.of(page, batchSize, Sort.by(Sort.Direction.ASC, "timestamp"));
			resultPage = moveEventDocumentRepository.findBySessionId(sessionId, pageRequest);
			allEvents.addAll(resultPage.getContent());
			page++;
		} while (!resultPage.isLast());

		return allEvents;
	}

	@Override
	public List<PointerScrollEventDocument> fetchAllScrollEventDocumentsBySessionId(String sessionId, int batchSize) {
		List<PointerScrollEventDocument> allEvents = new ArrayList<>();
		int page = 0;
		Page<PointerScrollEventDocument> resultPage;

		if (!scrollEventDocumentRepository.existsBySessionId(sessionId)) {
			log.info("해당 sessionId에 대한 이벤트 데이터가 없습니다: {}", sessionId);
			return Collections.emptyList();
		}

		do {
			PageRequest pageRequest = PageRequest.of(page, batchSize, Sort.by(Sort.Direction.ASC, "timestamp"));
			resultPage = scrollEventDocumentRepository.findBySessionId(sessionId, pageRequest);
			allEvents.addAll(resultPage.getContent());
			page++;
		} while (!resultPage.isLast());

		return allEvents;
	}

}
