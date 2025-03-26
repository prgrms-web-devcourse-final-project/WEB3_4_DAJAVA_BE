package com.dajava.backend.domain.event.scheduler;

import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.SessionData;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
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

	@BeforeEach
	void setUp() {
		sessionDataRepository = mock(SessionDataRepository.class);
		clickEventAnalyzer = mock(ClickEventAnalyzer.class);
		moveEventAnalyzer = mock(MoveEventAnalyzer.class);
		scrollEventAnalyzer = mock(ScrollEventAnalyzer.class);

		scheduler = new EventValidateScheduler(
			sessionDataRepository,
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
}
