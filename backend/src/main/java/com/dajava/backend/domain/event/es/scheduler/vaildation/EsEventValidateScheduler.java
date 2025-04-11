package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.converter.PointerEventConverter;
import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;
import com.dajava.backend.domain.event.es.repository.PointerClickEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerMoveEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerScrollEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.es.repository.SolutionEventDocumentRepository;
import com.dajava.backend.domain.event.es.service.PointerEventDocumentService;
import com.dajava.backend.domain.event.es.service.SessionDataDocumentService;
import com.dajava.backend.domain.event.es.service.SolutionEventDocumentService;
import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.global.component.analyzer.ValidateSchedulerProperties;
import com.dajava.backend.global.utils.EventsUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 검증 로직을 주기적으로 실행하는 스케줄러 입니다.
 * es에서 데이터를 조회합니다.
 *
 * @author NohDongHui
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EsEventValidateScheduler {

	private final SessionDataDocumentService sessionDataDocumentService;
	private final SolutionEventDocumentService solutionEventDocumentService;
	private final PointerEventDocumentService pointerEventDocumentService;

	private final EsClickEventAnalyzer esClickEventAnalyzer;
	private final EsMoveEventAnalyzer esMoveEventAnalyzer;
	private final EsScrollEventAnalyzer esScrollEventAnalyzer;

	private final ValidateSchedulerProperties validateSchedulerProperties;



	/**
	 * 주기적으로 es에서 세션 종료된 세션 데이터를 꺼내 검증합니다.
	 * 이상치 데이터는 isoutlier가 true로 저장되며
	 * click, move, scroll 이벤트 document는 soluitonEventDocument로 변환되어 es에 저장됩니다.
	 * 한번에 많은 데이터가 메모리에 들어오는 걸 대비해 배치 처리합니다.
	 * 배치 처리 구현에 페이징을 사용했습니다.
	 * 그래도 메모리 터지는 경우 최대 데이터 상한선을 설정해 스케줄러가 처리 가능한 데이터 제한
	 */
	@Scheduled(fixedRateString = "#{@validateSchedulerProperties.validateEndSessionMs}")
	public void endedSessionValidate() {

		log.info("검증 스케줄러 시작");

		int batchSize = validateSchedulerProperties.getBatchSize();
		int page = 0;

		Page<SessionDataDocument> resultPage;

		do {
			resultPage = sessionDataDocumentService.getEndedSessions(page, batchSize);

			log.info("Batch {}: SessionData size : {}", page, resultPage.getContent().size());

			for (SessionDataDocument sessionDataDocument : resultPage.getContent()) {
				try {
					processSession(sessionDataDocument);
				} catch (PointerEventException e) {
					log.warn("세션 검증 실패 (이미 검증된 세션일 수 있음): {}, {}", sessionDataDocument.getSessionId(), e.getMessage());
				} catch (Exception e) {
					log.error("예상치 못한 에러 발생 - 세션 ID: {}", sessionDataDocument.getSessionId(), e);
				}
			}

			page++;

			// 가비지 컬렉션 여유를 위해 짧은 sleep (optional)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // 인터럽트 상태 복구
				log.warn("Thread sleep interrupted", e);
			}

		} while (!resultPage.isLast());
	}

	/**
	 * 세션 검증을 진행하는 메소드로 sessionDataDocument의 sessionId로 각 클릭, 무브 ,스크롤
	 * 데이터를 조회합니다. 조회 시 timestamp를 기준으로 오름차순 합니다.
	 * 3종류 이벤트 데이터를 SolutionEventDocument로 통합시켜 저장합니다.
	 *
	 */
	public void processSession(SessionDataDocument sessionDataDocument) {

		if (sessionDataDocument.isVerified()) {
			log.info("이미 검증된 세션 데이터 입니다 sessionId : {}", sessionDataDocument.getSessionId());
			return;
		}

		String sessionId = sessionDataDocument.getSessionId();
		log.info("검증 되는 세션 아이디 : {}", sessionId);

		int batchSize = validateSchedulerProperties.getBatchSize();
		List<PointerClickEventDocument> clickEvents = pointerEventDocumentService.fetchAllClickEventDocumentsBySessionId(sessionId, batchSize);
		List<PointerMoveEventDocument> moveEvents = pointerEventDocumentService.fetchAllMoveEventDocumentsBySessionId(sessionId, batchSize);
		List<PointerScrollEventDocument> scrollEvents = pointerEventDocumentService.fetchAllScrollEventDocumentsBySessionId(sessionId, batchSize);

		// null 값 있음 안되니 필터링
		EventsUtils.filterValidClickEvents(clickEvents);
		EventsUtils.filterValidMoveEvents(moveEvents);
		EventsUtils.filterValidScrollEvents(scrollEvents);

		log.info("검증 되는 clickEventsDocument 개수 : {}", clickEvents.size());
		log.info("검증 되는 moveEventsDocument 개수 : {}", moveEvents.size());
		log.info("검증 되는 scrollEventsDocument 개수 : {}", scrollEvents.size());

		esClickEventAnalyzer.analyze(clickEvents);
		esMoveEventAnalyzer.analyze(moveEvents);
		esScrollEventAnalyzer.analyze(scrollEvents);

		sessionDataDocument.markAsVerified();

		log.info("검증 완료");

		List<SolutionEventDocument> solutionEvents = PointerEventConverter.toSolutionEventDocuments(
			clickEvents, moveEvents, scrollEvents);

		solutionEventDocumentService.saveAllSolutionEvents(solutionEvents);
		log.info("저장된 SolutionEventDocument 개수 : {}", solutionEvents.size());
		sessionDataDocumentService.save(sessionDataDocument);
	}

}
