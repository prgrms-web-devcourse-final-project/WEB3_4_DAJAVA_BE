package com.dajava.backend.global.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Getter
public class EventRedisBuffer {
	private final EventQueueRedisBuffer<PointerClickEventRequest> clickBuffer;
	private final EventQueueRedisBuffer<PointerMoveEventRequest> moveBuffer;
	private final EventQueueRedisBuffer<PointerScrollEventRequest> scrollBuffer;

	// add
	public void addClickEvent(PointerClickEventRequest event, SessionDataKey sessionDataKey) {
		clickBuffer.addEvent(sessionDataKey, event);
	}

	public void addMoveEvent(PointerMoveEventRequest event, SessionDataKey sessionDataKey) {
		moveBuffer.addEvent(sessionDataKey, event);
	}

	public void addScrollEvent(PointerScrollEventRequest event, SessionDataKey sessionDataKey) {
		scrollBuffer.addEvent(sessionDataKey, event);
	}

	// get
	public List<PointerClickEventRequest> getClickEvents(SessionDataKey sessionDataKey) {
		return clickBuffer.getEvents(sessionDataKey);
	}

	public List<PointerMoveEventRequest> getMoveEvents(SessionDataKey sessionDataKey) {
		return moveBuffer.getEvents(sessionDataKey);
	}

	public List<PointerScrollEventRequest> getScrollEvents(SessionDataKey sessionDataKey) {
		return scrollBuffer.getEvents(sessionDataKey);
	}

	// flush
	public List<PointerClickEventRequest> flushClickEvents(SessionDataKey sessionDataKey) {
		return clickBuffer.flushEvents(sessionDataKey);
	}

	public List<PointerMoveEventRequest> flushMoveEvents(SessionDataKey sessionDataKey) {
		return moveBuffer.flushEvents(sessionDataKey);
	}

	public List<PointerScrollEventRequest> flushScrollEvents(SessionDataKey sessionDataKey) {
		return scrollBuffer.flushEvents(sessionDataKey);
	}

	// 전체 클리어
	public void clearAll() {
		clickBuffer.clearAll();
		moveBuffer.clearAll();
		scrollBuffer.clearAll();
	}

	// 활성 세션 목록 반환 메서드
	public Set<SessionDataKey> getAllActiveSessionKeys() {
		Set<SessionDataKey> activeSessionKeys = new HashSet<>();

		activeSessionKeys.addAll(clickBuffer.getActiveSessionKeys());
		activeSessionKeys.addAll(moveBuffer.getActiveSessionKeys());
		activeSessionKeys.addAll(scrollBuffer.getActiveSessionKeys());

		return activeSessionKeys;
	}
}
