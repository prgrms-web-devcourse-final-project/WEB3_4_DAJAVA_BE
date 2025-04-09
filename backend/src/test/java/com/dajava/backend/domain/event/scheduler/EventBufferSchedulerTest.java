package com.dajava.backend.domain.event.scheduler;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.service.ActivityHandleService;
import com.dajava.backend.domain.event.service.EventBatchService;
import com.dajava.backend.global.component.analyzer.BufferSchedulerProperties;
import com.dajava.backend.global.component.buffer.EventBuffer;

/*
 * 버퍼에서 리포지드로 저장하는 스케줄러 통합테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-03-26
 */
public class EventBufferSchedulerTest {

	private EventBuffer eventBuffer;
	private EventBatchService eventBatchService;
	private EventBufferScheduler scheduler;
	private ActivityHandleService activityHandleService;

	final long inactivityThresholdMs = 10 * 60 * 1000; // 10분

	@BeforeEach
	void setUp() {
		BufferSchedulerProperties props = new BufferSchedulerProperties();
		props.setInactiveSessionDetectThresholdMs("60000"); // String
		props.setActiveSessionFlushIntervalMs("300000");    // String
		props.setInactiveThresholdMs(600000L);
		activityHandleService = mock(ActivityHandleService.class);  // EventBatchService mock 추가
		eventBuffer = new EventBuffer();
		scheduler = new EventBufferScheduler(eventBuffer, activityHandleService,props);
	}

	@Test
	@DisplayName("스케줄러 타임아웃 통합 테스트")
	void t1() {
		// given
		SessionDataKey oldSessionKey = new SessionDataKey("session1", "https://example.com", "user001");
		SessionDataKey activeSessionKey = new SessionDataKey("session2", "https://example.com", "user002");

		PointerClickEventRequest oldEvent = new PointerClickEventRequest(
			"user1", "session1", "https://example.com", "user001",
			System.currentTimeMillis(), 1920, 100, 200, 100, 1000, 100, "div"
		);

		PointerClickEventRequest activeEvent = new PointerClickEventRequest(
			"user2", "session2", "https://example.com", "user002",
			System.currentTimeMillis(), 1920, 300, 400, 100, 1000, 100, "div"
		);

		// 비활성 세션 이벤트 추가
		eventBuffer.getClickBuffer().addEvent(oldSessionKey, oldEvent);
		eventBuffer.getClickBuffer().getLastUpdatedMap().put(
			"session1|https://example.com|user001",
			System.currentTimeMillis() - inactivityThresholdMs * 2
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
		// 이제 eventLogService 대신 activityHandleService를 검증
		verify(activityHandleService, times(1)).processInactiveBatchForSession(eq(oldSessionKey));

		// 활성 세션은 처리되지 않았는지 확인
		verify(activityHandleService, never()).processActiveBatchForSession(eq(activeSessionKey));

		// 활성 세션 이벤트는 여전히 남아 있음
		List<PointerClickEventRequest> remaining = eventBuffer.getClickBuffer().getEvents(activeSessionKey);
		assertThat(remaining).containsExactly(activeEvent);
	}
}

