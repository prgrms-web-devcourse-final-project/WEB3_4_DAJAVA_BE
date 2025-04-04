package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.converter.PointerEventConverter;
import com.dajava.backend.domain.event.entity.PointerClickEvent;
import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;
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
import com.dajava.backend.domain.event.exception.PointerEventException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 검증 로직을 주기적으로 실행하는 스케줄러 입니다.
 * es에서 데이터를 조회합니다.
 * @author NohDongHui
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EsEventValidateScheduler {

	private final SessionDataDocumentRepository sessionDataDocumentRepository;
	private final SolutionEventDocumentRepository solutionEventDocumentRepository;
	private final PointerClickEventDocumentRepository clickEventDocumentRepository;
	private final PointerMoveEventDocumentRepository moveEventDocumentRepository;
	private final PointerScrollEventDocumentRepository scrollEventDocumentRepository;

	private final EsClickEventAnalyzer esClickEventAnalyzer;
	private final EsMoveEventAnalyzer esMoveEventAnalyzer;
	private final EsScrollEventAnalyzer esScrollEventAnalyzer;

	// 비활성 상태 간주 시간 (10분)
	private static final long VALIDATE_END_SESSION_MS = 10L * 60 * 1000;

	//배치 사이즈
	private static final int BATCH_SIZE = 500;

	/**
	 * 주기적으로 es에서 세션 종료된 세션 데이터를 꺼내 검증합니다.
	 * 이상치 데이터는 isoutlier가 true로 저장되며
	 * click, move, scroll 이벤트 document는 soluitonEventDocument로 변환되어 es에 저장됩니다.
	 * 한번에 많은 데이터가 메모리에 들어오는 걸 대비해 배치 처리합니다.
	 */
	@Scheduled(fixedRate = VALIDATE_END_SESSION_MS)
	public void endedSessionValidate() {

		int page = 0;
		Page<SessionDataDocument> resultPage;

		do {
			PageRequest pageRequest = PageRequest.of(page, BATCH_SIZE);
			resultPage = sessionDataDocumentRepository.findByIsSessionEndedTrue(pageRequest);

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


	public void processSession(SessionDataDocument sessionDataDocument) {
		String sessionId = sessionDataDocument.getSessionId();

		List<PointerClickEventDocument> clickEvents = clickEventDocumentRepository.findBySessionId(
			sessionId,
			Sort.by(Sort.Direction.ASC, "timestamp")
		);
		List<PointerMoveEventDocument> moveEvents = moveEventDocumentRepository.findBySessionId(
			sessionId,
			Sort.by(Sort.Direction.ASC, "timestamp")
		);
		List<PointerScrollEventDocument> scrollEvents = scrollEventDocumentRepository.findBySessionId(
			sessionId,
			Sort.by(Sort.Direction.ASC, "timestamp")
		);

		esClickEventAnalyzer.analyze(clickEvents);
		esMoveEventAnalyzer.analyze(moveEvents);
		esScrollEventAnalyzer.analyze(scrollEvents);

		sessionDataDocument.markAsVerified(); // 예외 발생 가능

		List<SolutionEventDocument> solutionEvents = PointerEventConverter.toSolutionEventDocuments(
			clickEvents, moveEvents, scrollEvents);

		solutionEventDocumentRepository.saveAll(solutionEvents);
	}
}
