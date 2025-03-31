package com.dajava.backend.domain.event.scheduler;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.entity.PointerClickEvent;
import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.domain.event.repository.SolutionDataRepository;
import com.dajava.backend.domain.event.scheduler.vaildation.ClickEventAnalyzer;
import com.dajava.backend.domain.event.scheduler.vaildation.MoveEventAnalyzer;
import com.dajava.backend.domain.event.scheduler.vaildation.ScrollEventAnalyzer;

/*
 * 리포지드 데이터를 검증해 이상치를 분류하는 스케줄러 통합테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-03-26
 */
public class EventVaildateSchedulerTest {

	private EventValidateScheduler scheduler;

	private SessionDataRepository sessionDataRepository;
	private ClickEventAnalyzer clickEventAnalyzer;
	private MoveEventAnalyzer moveEventAnalyzer;
	private ScrollEventAnalyzer scrollEventAnalyzer;
	private SolutionDataRepository solutionDataRepository;

	@BeforeEach
	void setUp() {
		sessionDataRepository = mock(SessionDataRepository.class);
		clickEventAnalyzer = mock(ClickEventAnalyzer.class);
		moveEventAnalyzer = mock(MoveEventAnalyzer.class);
		scrollEventAnalyzer = mock(ScrollEventAnalyzer.class);
		solutionDataRepository = mock(SolutionDataRepository.class);
		scheduler = new EventValidateScheduler(
			sessionDataRepository,
			solutionDataRepository,
			clickEventAnalyzer,
			moveEventAnalyzer,
			scrollEventAnalyzer
		);
	}

	@Test
	@DisplayName("종료된 세션이 존재할 때 각 analyzer가 호출되어야 함")
	void shouldCallAnalyzersForEachEndedSession() {
		// given
		SessionData session1 = mock(SessionData.class);
		SessionData session2 = mock(SessionData.class);
		List<SessionData> endedSessions = List.of(session1, session2);

		when(sessionDataRepository.findEndedSession()).thenReturn(endedSessions);

		// when
		scheduler.endedSessionValidate();

		// then
		verify(sessionDataRepository, times(1)).findEndedSession();
		for (SessionData session : endedSessions) {
			verify(clickEventAnalyzer).analyze(session);
			verify(moveEventAnalyzer).analyze(session);
			verify(scrollEventAnalyzer).analyze(session);
		}
	}

	@Test
	@DisplayName("종료된 세션이 없을 경우 analyzer는 호출되지 않아야 함")
	void shouldNotCallAnalyzersIfNoEndedSession() {
		// given
		when(sessionDataRepository.findEndedSession()).thenReturn(Collections.emptyList());

		// when
		scheduler.endedSessionValidate();

		// then
		verify(sessionDataRepository).findEndedSession();
		verifyNoInteractions(clickEventAnalyzer, moveEventAnalyzer, scrollEventAnalyzer);
	}

	@Test
	@DisplayName("분석된 이벤트가 SolutionData에 저장되어야 함")
	void shouldSaveAnalyzedEventsToSolutionData() {
		// given
		SessionData session = mock(SessionData.class);
		when(session.getMemberSerialNumber()).thenReturn("user123");

		List<PointerClickEvent> clickEvents = List.of(mock(PointerClickEvent.class));
		List<PointerMoveEvent> moveEvents = List.of(mock(PointerMoveEvent.class));
		List<PointerScrollEvent> scrollEvents = List.of(mock(PointerScrollEvent.class));

		when(sessionDataRepository.findEndedSession()).thenReturn(List.of(session));
		when(clickEventAnalyzer.analyze(session)).thenReturn(clickEvents);
		when(moveEventAnalyzer.analyze(session)).thenReturn(moveEvents);
		when(scrollEventAnalyzer.analyze(session)).thenReturn(scrollEvents);

		// when
		scheduler.endedSessionValidate();

		// then
		verify(solutionDataRepository, times(1)).save(argThat(solutionData ->
			solutionData.getSerialNumber().equals("user123") &&
				!solutionData.getSolutionEvents().isEmpty()
		));
	}

	@Test
	@DisplayName("분석 후 세션은 검증 완료 상태로 변경되어야 함")
	void shouldSetSessionAsVerifiedAfterAnalysis() {
		// given
		SessionData session = mock(SessionData.class);
		when(session.getMemberSerialNumber()).thenReturn("user123");

		when(sessionDataRepository.findEndedSession()).thenReturn(List.of(session));
		when(clickEventAnalyzer.analyze(session)).thenReturn(Collections.emptyList());
		when(moveEventAnalyzer.analyze(session)).thenReturn(Collections.emptyList());
		when(scrollEventAnalyzer.analyze(session)).thenReturn(Collections.emptyList());

		// when
		scheduler.endedSessionValidate();

		// then
		verify(session, times(1)).setVerified();
	}

	@Test
	@DisplayName("이벤트 개수만큼 SolutionEvent가 생성되어야 함")
	void shouldConvertAllEventsToSolutionEvents() {
		// given
		SessionData session = mock(SessionData.class);
		when(session.getMemberSerialNumber()).thenReturn("user123");

		PointerClickEvent click = mock(PointerClickEvent.class);
		when(click.getSessionId()).thenReturn("s");
		when(click.getPageUrl()).thenReturn("p");
		when(click.getCreateDate()).thenReturn(LocalDateTime.now());
		when(click.getBrowserWidth()).thenReturn(1080);

		when(sessionDataRepository.findEndedSession()).thenReturn(List.of(session));
		when(clickEventAnalyzer.analyze(session)).thenReturn(List.of(click));
		when(moveEventAnalyzer.analyze(session)).thenReturn(Collections.emptyList());
		when(scrollEventAnalyzer.analyze(session)).thenReturn(Collections.emptyList());

		// when
		scheduler.endedSessionValidate();

		// then
		verify(solutionDataRepository).save(argThat(solutionData ->
			solutionData.getSolutionEvents().size() == 1 &&
				solutionData.getSolutionEvents().get(0).getType().equals("click")
		));
	}

}
