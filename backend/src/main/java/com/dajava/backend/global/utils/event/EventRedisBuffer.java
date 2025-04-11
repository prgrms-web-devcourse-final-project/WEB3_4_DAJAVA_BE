package com.dajava.backend.global.utils.event;

import java.util.List;

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

	// 클릭
	public void addClickEvent(ClickEventRequest event, SessionIdentifier sessionIdentifier) {
		click.cacheEvents(sessionIdentifier, event);
	}

	public List<ClickEventRequest> getClickEvents(SessionIdentifier sessionIdentifier) {
		return click.getEvents(sessionIdentifier, new ClickEventRequest());
	}

	public List<ClickEventRequest> flushClickEvents(SessionIdentifier sessionIdentifier) {
		return click.flushEvents(sessionIdentifier, new ClickEventRequest());
	}

	// 무브
	public void addMoveEvent(MovementEventRequest event, SessionIdentifier sessionIdentifier) {
		movement.cacheEvents(sessionIdentifier, event);
	}

	public List<MovementEventRequest> getMoveEvents(SessionIdentifier sessionIdentifier) {
		return movement.getEvents(sessionIdentifier, new MovementEventRequest());
	}

	public List<MovementEventRequest> flushMoveEvents(SessionIdentifier sessionIdentifier) {
		return movement.flushEvents(sessionIdentifier, new MovementEventRequest());
	}

	// 스크롤
	public void addScrollEvent(ScrollEventRequest event, SessionIdentifier sessionIdentifier) {
		scroll.cacheEvents(sessionIdentifier, event);
	}

	public List<ScrollEventRequest> getScrollEvents(SessionIdentifier sessionIdentifier) {
		return scroll.getEvents(sessionIdentifier, new ScrollEventRequest());
	}

	public List<ScrollEventRequest> flushScrollEvents(SessionIdentifier sessionIdentifier) {
		return scroll.flushEvents(sessionIdentifier, new ScrollEventRequest());
	}
}
