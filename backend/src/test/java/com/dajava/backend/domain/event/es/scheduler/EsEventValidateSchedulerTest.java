package com.dajava.backend.domain.event.es.scheduler;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

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
import com.dajava.backend.domain.event.es.scheduler.vaildation.EsClickEventAnalyzer;
import com.dajava.backend.domain.event.es.scheduler.vaildation.EsEventValidateScheduler;
import com.dajava.backend.domain.event.es.scheduler.vaildation.EsMoveEventAnalyzer;
import com.dajava.backend.domain.event.es.scheduler.vaildation.EsScrollEventAnalyzer;

/*
 * es 리포지드에서 데이터를 꺼내 검증하는 스케줄러 통합테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-04-04
 */
@ExtendWith(MockitoExtension.class)
class EsEventValidateSchedulerTest {

	@Mock private SessionDataDocumentRepository sessionRepo;
	@Mock private SolutionEventDocumentRepository solutionRepo;
	@Mock private PointerClickEventDocumentRepository clickRepo;
	@Mock private PointerMoveEventDocumentRepository moveRepo;
	@Mock private PointerScrollEventDocumentRepository scrollRepo;

	@Mock private EsClickEventAnalyzer clickAnalyzer;
	@Mock private EsMoveEventAnalyzer moveAnalyzer;
	@Mock private EsScrollEventAnalyzer scrollAnalyzer;

	@InjectMocks
	private EsEventValidateScheduler scheduler;

	@Test
	@DisplayName("processSession이 정상적으로 동작하는 경우")
	void testProcessSession_success() {
		// given
		SessionDataDocument session = mock(SessionDataDocument.class);
		when(session.getSessionId()).thenReturn("test-session");

		List<PointerClickEventDocument> clickEvents = List.of(
			PointerClickEventDocument.builder().isOutlier(false).timestamp(LocalDateTime.now()).build()
		);
		List<PointerMoveEventDocument> moveEvents = List.of(
			PointerMoveEventDocument.builder().isOutlier(false).timestamp(LocalDateTime.now()).build()
		);
		List<PointerScrollEventDocument> scrollEvents = List.of(
			PointerScrollEventDocument.builder().isOutlier(false).timestamp(LocalDateTime.now()).build()
		);

		when(clickRepo.findBySessionId(eq("test-session"), any(Sort.class))).thenReturn(clickEvents);
		when(moveRepo.findBySessionId(eq("test-session"), any(Sort.class))).thenReturn(moveEvents);
		when(scrollRepo.findBySessionId(eq("test-session"), any(Sort.class))).thenReturn(scrollEvents);

		List<SolutionEventDocument> solutionDocs = List.of(
			mock(SolutionEventDocument.class)
		);

		// static method mock (PointerEventConverter)
		try (MockedStatic<PointerEventConverter> mocked = mockStatic(PointerEventConverter.class)) {
			mocked.when(() -> PointerEventConverter.toSolutionEventDocuments(clickEvents, moveEvents, scrollEvents))
				.thenReturn(solutionDocs);

			// when
			scheduler.processSession(session);

			// then
			verify(clickAnalyzer).analyze(clickEvents);
			verify(moveAnalyzer).analyze(moveEvents);
			verify(scrollAnalyzer).analyze(scrollEvents);
			verify(session).markAsVerified();
			verify(solutionRepo).saveAll(solutionDocs);
		}
	}
}