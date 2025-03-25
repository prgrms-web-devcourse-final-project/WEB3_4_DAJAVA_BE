package com.dajava.backend.domain.event.scheduler;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.service.EventBatchService;
import com.dajava.backend.domain.event.service.EventLogService;
import com.dajava.backend.global.component.buffer.EventBuffer;

/*
 * 스케줄러 통합테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-03-24
 */
public class EventBufferSchedulerTest {

	private EventLogService eventLogService;
	private EventBuffer eventBuffer;
	private EventBatchService eventBatchService;
	private EventBufferScheduler scheduler;

	final long inactivityThresholdMs = 10 * 60 * 1000; // 10분

	@BeforeEach
	void setUp() {
		eventLogService = mock(EventLogService.class);
		eventBuffer = new EventBuffer();
		scheduler = new EventBufferScheduler(eventLogService, eventBatchService, eventBuffer);
	}

	@Test
	@DisplayName("스케줄러 타임아웃 통합 테스트 ")
	void t1() {
		// given
		SessionDataKey oldSessionKey = new SessionDataKey("session1", "https://example.com", "user001");
		SessionDataKey activeSessionKey = new SessionDataKey("session2", "https://example.com", "user002");

		PointerClickEventRequest oldEvent = new PointerClickEventRequest(
			"session1", "https://example.com", "user001",
			System.currentTimeMillis(), 1920, 100, 200
		);

		PointerClickEventRequest activeEvent = new PointerClickEventRequest(
			"session2", "https://example.com", "user002",
			System.currentTimeMillis(), 1920, 300, 400
		);

		// 비활성 세션 이벤트 추가
		eventBuffer.getClickBuffer().addEvent(oldSessionKey, oldEvent);
		eventBuffer.getClickBuffer().getLastUpdatedMap().put(
			"session1|https://example.com|user001",
			System.currentTimeMillis() - inactivityThresholdMs - 1
		);

		// 활성 세션 이벤트 추가
		eventBuffer.getClickBuffer().addEvent(activeSessionKey, activeEvent);
		eventBuffer.getClickBuffer().getLastUpdatedMap().put(
			"session2|https://example.com|user002",
			System.currentTimeMillis()
		);

		// when
		scheduler.flushInactiveEventBuffers();

		// then
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(eventLogService, times(1)).saveAll(captor.capture());

		List<PointerClickEventRequest> flushedEvents = captor.getValue();
		assertThat(flushedEvents).containsExactly(oldEvent);

		// 활성 세션 이벤트는 여전히 남아 있음
		List<PointerClickEventRequest> remaining = eventBuffer.getClickBuffer().getEvents(activeSessionKey);
		assertThat(remaining).containsExactly(activeEvent);
	}
}
