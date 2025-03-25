package com.dajava.backend.domain.event.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.dajava.backend.domain.event.PointerClickEvent;
import com.dajava.backend.domain.event.SessionData;
import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.global.component.buffer.EventBuffer;


/*
 * 로그 데이터를 리포지드에 저장하는 로그 서비스 단위 테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-03-24
 */
public class EventLogServiceTest {

	private SessionDataRepository sessionDataRepository;
	private SessionDataService sessionDataService;
	private EventBuffer eventBuffer;
	private EventLogService eventLogService;


	@BeforeEach
	void setUp() {
		sessionDataRepository = mock(SessionDataRepository.class);
		sessionDataService = mock(SessionDataService.class);
		eventBuffer = mock(EventBuffer.class);
		eventLogService = new EventLogServiceImpl(sessionDataRepository, sessionDataService, eventBuffer);
	}

	@Test
	@DisplayName("로그 이벤트 저장 테스트")
	void t1() {
		// given
		SessionData sessionData = SessionData.create(
			"https://example.com",
			"session123",
			"user001"
		);

		PointerClickEventRequest request = new PointerClickEventRequest(
			"session123", "https://example.com", "user001",
			System.currentTimeMillis(), 1920, 100, 200
		);

		when(sessionDataRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
			request.pageUrl(), request.sessionId(), request.memberSerialNumber())
		).thenReturn(Optional.of(sessionData));

		// when
		eventLogService.saveClickEvents(List.of(request));

		// then
		// SessionData에 이벤트가 들어갔는지 확인
		assertThat(sessionData.getPointerClickEvents()).hasSize(1);

		PointerClickEvent savedEvent = sessionData.getPointerClickEvents().getFirst();

		assertThat(savedEvent.getClientX()).isEqualTo(100);
		assertThat(savedEvent.getClientY()).isEqualTo(200);
		assertThat(savedEvent.getPageUrl()).isEqualTo("https://example.com");

		// session 저장이 호출됐는지 확인
		verify(sessionDataRepository).save(sessionData);
	}

	@Test
	@DisplayName("만약 매칭되는 세션 정보가 없을 경우 저장하지 않음 ")
	void t2() {
		// given
		PointerClickEventRequest request = new PointerClickEventRequest(
			"invalidSession", "https://example.com", "user999",
			System.currentTimeMillis(), 1440, 120, 250
		);

		when(sessionDataRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
			request.pageUrl(), request.sessionId(), request.memberSerialNumber())
		).thenReturn(Optional.empty());

		// when
		eventLogService.saveClickEvents(List.of(request));

		// then
		verify(sessionDataRepository, never()).save(any());
	}
}
