package com.dajava.backend.global.utils.event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;
import com.dajava.backend.global.utils.session.ActiveSessionManager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Getter
public class EventRedisBuffer {
	private final EventQueueRedisBuffer<ClickEventRequest> click;
	private final EventQueueRedisBuffer<MovementEventRequest> movement;
	private final EventQueueRedisBuffer<ScrollEventRequest> scroll;
	private final ActiveSessionManager activeSessionManager;

	// add
	public void addClickEvent(ClickEventRequest event, SessionIdentifier sessionIdentifier) {
		click.cacheEvents(sessionIdentifier, event);
	}

	public void addMoveEvent(MovementEventRequest event, SessionIdentifier sessionIdentifier) {
		movement.cacheEvents(sessionIdentifier, event);
	}

	public void addScrollEvent(ScrollEventRequest event, SessionIdentifier sessionIdentifier) {
		scroll.cacheEvents(sessionIdentifier, event);
	}

	// get
	public List<ClickEventRequest> getClickEvents(SessionIdentifier sessionIdentifier) {
		return click.getEvents(sessionIdentifier);
	}

	public List<MovementEventRequest> getMoveEvents(SessionIdentifier sessionIdentifier) {
		return movement.getEvents(sessionIdentifier);
	}

	public List<ScrollEventRequest> getScrollEvents(SessionIdentifier sessionIdentifier) {
		return scroll.getEvents(sessionIdentifier);
	}

	// flush
	public List<ClickEventRequest> flushClickEvents(SessionIdentifier sessionIdentifier) {
		return click.flushEvents(sessionIdentifier);
	}

	public List<MovementEventRequest> flushMoveEvents(SessionIdentifier sessionIdentifier) {
		return movement.flushEvents(sessionIdentifier);
	}

	public List<ScrollEventRequest> flushScrollEvents(SessionIdentifier sessionIdentifier) {
		return scroll.flushEvents(sessionIdentifier);
	}

	// 활성 세션 목록 반환 메서드
	public Set<SessionIdentifier> getAllActiveSessionKeys() {
		Set<SessionIdentifier> activeSessionKeys = new HashSet<>();
		activeSessionKeys.addAll(activeSessionManager.getActiveSessionKeysForType("click:"));
		activeSessionKeys.addAll(activeSessionManager.getActiveSessionKeysForType("move:"));
		activeSessionKeys.addAll(activeSessionManager.getActiveSessionKeysForType("scroll:"));
		return activeSessionKeys;
	}
}
