package com.dajava.backend.global.component.buffer;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;

import lombok.Getter;
/**
 * 리포지드에 저장하기 전 이벤트 데이터를 임시 저장하는 버퍼 데이터 구조 입니다.
 */
@Getter
@Component
public class EventBuffer {

	private final EventQueueBuffer<PointerClickEventRequest> clickBuffer = new EventQueueBuffer<>();
	private final EventQueueBuffer<PointerMoveEventRequest> moveBuffer = new EventQueueBuffer<>();
	private final EventQueueBuffer<PointerScrollEventRequest> scrollBuffer = new EventQueueBuffer<>();

	// add
	public void addClickEvent(PointerClickEventRequest event, SessionDataKey sessionDataKey) {
		clickBuffer.addEvent( sessionDataKey, event);
	}

	public void addMoveEvent(PointerMoveEventRequest event, SessionDataKey sessionDataKey) {
		moveBuffer.addEvent( sessionDataKey, event);
	}

	public void addScrollEvent(PointerScrollEventRequest event, SessionDataKey sessionDataKey) {
		scrollBuffer.addEvent( sessionDataKey, event);
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


}
