package com.dajava.backend.domain.event.es.scheduler;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dajava.backend.domain.event.converter.PointerEventConverter;
import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;
import com.dajava.backend.domain.event.es.scheduler.vaildation.EsClickEventAnalyzer;
import com.dajava.backend.domain.event.es.scheduler.vaildation.EsEventValidateScheduler;
import com.dajava.backend.domain.event.es.scheduler.vaildation.EsMoveEventAnalyzer;
import com.dajava.backend.domain.event.es.scheduler.vaildation.EsScrollEventAnalyzer;
import com.dajava.backend.domain.event.es.service.PointerEventDocumentService;
import com.dajava.backend.domain.event.es.service.SessionDataDocumentService;
import com.dajava.backend.domain.event.es.service.SolutionEventDocumentService;
import com.dajava.backend.global.component.analyzer.ValidateSchedulerProperties;

/*
 * es 리포지드에서 데이터를 꺼내 검증하는 스케줄러 통합테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-04-04
 */
@ExtendWith(MockitoExtension.class)
class EsEventValidateSchedulerTest {

	@Mock private SessionDataDocumentService sessionDataDocumentService;
	@Mock private SolutionEventDocumentService solutionEventDocumentService;
	@Mock private PointerEventDocumentService pointerEventDocumentService;

	@Mock private EsClickEventAnalyzer esClickEventAnalyzer;
	@Mock private EsMoveEventAnalyzer esMoveEventAnalyzer;
	@Mock private EsScrollEventAnalyzer esScrollEventAnalyzer;

	@Mock private ValidateSchedulerProperties validateSchedulerProperties;

	@InjectMocks
	private EsEventValidateScheduler scheduler;

	@BeforeEach
	void setup() {
		when(validateSchedulerProperties.getBatchSize()).thenReturn(100);
	}

	@Test
	@DisplayName("processSession이 정상적으로 동작하는 경우")
	void testProcessSession_success() {
		// given
		String sessionId = "test-session";
		SessionDataDocument session = mock(SessionDataDocument.class);
		LocalDateTime now = LocalDateTime.now();
		long timestamp = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		when(session.getSessionId()).thenReturn(sessionId);

		List<PointerClickEventDocument> clickEvents = List.of(
			PointerClickEventDocument.builder().isOutlier(false).timestamp(timestamp).build()
		);
		List<PointerMoveEventDocument> moveEvents = List.of(
			PointerMoveEventDocument.builder().isOutlier(false).timestamp(timestamp).build()
		);
		List<PointerScrollEventDocument> scrollEvents = List.of(
			PointerScrollEventDocument.builder().isOutlier(false).timestamp(timestamp).build()
		);

		when(pointerEventDocumentService.fetchAllClickEventDocumentsBySessionId(eq(sessionId), anyInt())).thenReturn(clickEvents);
		when(pointerEventDocumentService.fetchAllMoveEventDocumentsBySessionId(eq(sessionId), anyInt())).thenReturn(moveEvents);
		when(pointerEventDocumentService.fetchAllScrollEventDocumentsBySessionId(eq(sessionId), anyInt())).thenReturn(scrollEvents);

		List<SolutionEventDocument> solutionDocs = List.of(mock(SolutionEventDocument.class));

		try (MockedStatic<PointerEventConverter> mocked = mockStatic(PointerEventConverter.class)) {
			mocked.when(() -> PointerEventConverter.toSolutionEventDocuments(clickEvents, moveEvents, scrollEvents))
				.thenReturn(solutionDocs);

			// when
			scheduler.processSession(session);

			// then
			verify(esClickEventAnalyzer).analyze(clickEvents);
			verify(esMoveEventAnalyzer).analyze(moveEvents);
			verify(esScrollEventAnalyzer).analyze(scrollEvents);
			verify(session).markAsVerified();
			verify(solutionEventDocumentService).saveAllSolutionEvents(solutionDocs);
		}
	}
}