package com.dajava.backend.global.component.buffer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
/*
 * 로그 이벤트를 임시 저장하는 버퍼 자료구조 테스트 입니다
 *
 * @author NohDongHui
 * @since 2025-03-24
 */
public class EventBufferTest {

	private EventBuffer eventBuffer;
	private SessionDataKey sessionKey;

	@BeforeEach
	void setUp() {
		eventBuffer = new EventBuffer();
		sessionKey = new SessionDataKey("session123", "https://example.com", "user001");
	}

	private PointerClickEventRequest createClickEvent(int clientX, int clientY) {
		return new PointerClickEventRequest(
			"session123",
			"https://example.com",
			"user001",
			System.currentTimeMillis(),
			1920,
			clientX,
			clientY
		);
	}

	@Test
	void t1() {
		PointerClickEventRequest event1 = createClickEvent(100, 200);
		PointerClickEventRequest event2 = createClickEvent(150, 250);

		eventBuffer.addClickEvent(event1, sessionKey);
		eventBuffer.addClickEvent(event2, sessionKey);

		List<PointerClickEventRequest> events = eventBuffer.getClickEvents(sessionKey);

		assertThat(events).containsExactly(event1, event2);
	}

	@Test
	void t2() {
		PointerClickEventRequest event = createClickEvent(300, 400);
		eventBuffer.addClickEvent(event, sessionKey);

		List<PointerClickEventRequest> flushed = eventBuffer.flushClickEvents(sessionKey);

		assertThat(flushed).containsExactly(event);

		// flush 이후엔 비어 있어야 함
		List<PointerClickEventRequest> afterFlush = eventBuffer.getClickEvents(sessionKey);
		assertThat(afterFlush).isEmpty();
	}

	@Test
	void testClearAllBuffers() {
		PointerClickEventRequest event = createClickEvent(500, 600);
		eventBuffer.addClickEvent(event, sessionKey);

		eventBuffer.clearAll();

		List<PointerClickEventRequest> events = eventBuffer.getClickEvents(sessionKey);
		assertThat(events).isEmpty();
	}
}
