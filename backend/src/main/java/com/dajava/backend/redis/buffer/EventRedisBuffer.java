package com.dajava.backend.redis.buffer;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.*;
import com.dajava.backend.redis.utils.EventQueueRedisBuffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Getter
public class EventRedisBuffer {

	private final EventQueueRedisBuffer<PointerClickEventRequest> clickBuffer;
	private final EventQueueRedisBuffer<PointerMoveEventRequest> moveBuffer;
	private final EventQueueRedisBuffer<PointerScrollEventRequest> scrollBuffer;

	// 공통 처리 메서드
	private <T> void addEvent(EventQueueRedisBuffer<T> buffer, T event, SessionDataKey key) {
		buffer.addEvent(key, event);
	}

	private <T> List<T> getEvents(EventQueueRedisBuffer<T> buffer, SessionDataKey key) {
		return buffer.getEvents(key);
	}

	private <T> List<T> flushEvents(EventQueueRedisBuffer<T> buffer, SessionDataKey key) {
		return buffer.flushEvents(key);
	}

	// add
	public void addClickEvent(PointerClickEventRequest event, SessionDataKey key) {
		addEvent(clickBuffer, event, key);
	}

	public void addMoveEvent(PointerMoveEventRequest event, SessionDataKey key) {
		addEvent(moveBuffer, event, key);
	}

	public void addScrollEvent(PointerScrollEventRequest event, SessionDataKey key) {
		addEvent(scrollBuffer, event, key);
	}

	// get
	public List<PointerClickEventRequest> getClickEvents(SessionDataKey key) {
		return getEvents(clickBuffer, key);
	}

	public List<PointerMoveEventRequest> getMoveEvents(SessionDataKey key) {
		return getEvents(moveBuffer, key);
	}

	public List<PointerScrollEventRequest> getScrollEvents(SessionDataKey key) {
		return getEvents(scrollBuffer, key);
	}

	// flush
	public List<PointerClickEventRequest> flushClickEvents(SessionDataKey key) {
		return flushEvents(clickBuffer, key);
	}

	public List<PointerMoveEventRequest> flushMoveEvents(SessionDataKey key) {
		return flushEvents(moveBuffer, key);
	}

	public List<PointerScrollEventRequest> flushScrollEvents(SessionDataKey key) {
		return flushEvents(scrollBuffer, key);
	}

	// 전체 클리어
	public void clearAll() {
		List.of(clickBuffer, moveBuffer, scrollBuffer).forEach(EventQueueRedisBuffer::clearAll);
	}

	// 활성 세션 키 통합
	public Set<SessionDataKey> getAllActiveSessionKeys() {
		return List.of(clickBuffer, moveBuffer, scrollBuffer).stream()
			.flatMap(buffer -> buffer.getActiveSessionKeys().stream())
			.collect(Collectors.toSet());
	}
}
