package com.dajava.backend.global.utils.event;

import java.util.*;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.log.dto.*;
import com.dajava.backend.global.utils.session.ActiveSessionManager;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

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

	public void addClickEvent(ClickEventRequest event, SessionIdentifier sessionIdentifier) {
		addEvent(click, event, sessionIdentifier);
	}

	public void addMoveEvent(MovementEventRequest event, SessionIdentifier sessionIdentifier) {
		addEvent(movement, event, sessionIdentifier);
	}

	public void addScrollEvent(ScrollEventRequest event, SessionIdentifier sessionIdentifier) {
		addEvent(scroll, event, sessionIdentifier);
	}

	/// /
	public List<ClickEventRequest> getClickEvents(SessionIdentifier sessionIdentifier) {
		return getEvents(click, sessionIdentifier);
	}

	public List<MovementEventRequest> getMoveEvents(SessionIdentifier sessionIdentifier) {
		return getEvents(movement, sessionIdentifier);
	}

	public List<ScrollEventRequest> getScrollEvents(SessionIdentifier sessionIdentifier) {
		return getEvents(scroll, sessionIdentifier);
	}

	public List<ClickEventRequest> flushClickEvents(SessionIdentifier sessionIdentifier) {
		return flushEvents(click, sessionIdentifier);
	}

	public List<MovementEventRequest> flushMoveEvents(SessionIdentifier sessionIdentifier) {
		return flushEvents(movement, sessionIdentifier);
	}

	public List<ScrollEventRequest> flushScrollEvents(SessionIdentifier sessionIdentifier) {
		return flushEvents(scroll, sessionIdentifier);
	}

	private <T> void addEvent(EventQueueRedisBuffer<T> buffer, T event, SessionIdentifier sessionIdentifier) {
		buffer.cacheEvents(sessionIdentifier, event);
	}

	private <T> List<T> getEvents(EventQueueRedisBuffer<T> buffer, SessionIdentifier sessionIdentifier) {
		return buffer.getEvents(sessionIdentifier);
	}

	private <T> List<T> flushEvents(EventQueueRedisBuffer<T> buffer, SessionIdentifier sessionIdentifier) {
		return buffer.flushEvents(sessionIdentifier);
	}

}
