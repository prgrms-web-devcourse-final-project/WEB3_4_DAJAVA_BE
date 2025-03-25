package com.dajava.backend.global.component.buffer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
/*
 * 이벤트 버퍼 내부 제네릭 해시맵 구조를 테스트 합니다
 *
 * @author NohDongHui
 * @since 2025-03-24
 */
public class EventQueueBufferTest {

	private EventQueueBuffer<PointerClickEventRequest> buffer;
	private SessionDataKey sessionKey;

	@BeforeEach
	void setUp() {
		buffer = new EventQueueBuffer<>();
		sessionKey = new SessionDataKey("session123", "https://example.com", "user001");
	}

	@Test
	void t1() {
		PointerClickEventRequest event1 = new PointerClickEventRequest(
			"session123", "https://example.com", "user001",
			System.currentTimeMillis(), 1920, 100, 200
		);

		PointerClickEventRequest event2 = new PointerClickEventRequest(
			"session123", "https://example.com", "user001",
			System.currentTimeMillis(), 1920, 150, 250
		);

		buffer.addEvent(sessionKey, event1);
		buffer.addEvent(sessionKey, event2);

		List<PointerClickEventRequest> events = buffer.getEvents(sessionKey);

		assertThat(events).containsExactly(event1, event2);
	}

	@Test
	void t2() {
		PointerClickEventRequest event = new PointerClickEventRequest(
			"session123", "https://example.com", "user001",
			System.currentTimeMillis(), 1280, 50, 80
		);

		buffer.addEvent(sessionKey, event);

		List<PointerClickEventRequest> flushed = buffer.flushEvents(sessionKey);

		assertThat(flushed).containsExactly(event);

		// Flush 이후에는 비어 있어야 함
		assertThat(buffer.getEvents(sessionKey)).isEmpty();
	}
}
