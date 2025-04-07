package com.dajava.backend.redis.utill;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.global.component.buffer.EventQueueBuffer;
import com.dajava.backend.redis.controller.EventQueueRedisBuffer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 리포지드에 저장하기 전 이벤트 데이터를 임시 저장하는 버퍼 데이터 구조 입니다.
 */

@Component
@Getter
@RequiredArgsConstructor
public class RedisEventBuffer {
	private final EventQueueRedisBuffer<PointerClickEventRequest> clickBuffer;
	private final EventQueueRedisBuffer<PointerClickEventRequest> moveBuffer;
	private final EventQueueRedisBuffer<PointerClickEventRequest> scrollBuffer;

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

