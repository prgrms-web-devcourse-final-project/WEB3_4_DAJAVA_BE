package com.dajava.backend.global.component.buffer;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;

import lombok.Getter;

@Getter
@Component
public class EventBuffer {

	private final EventQueueBuffer<PointerClickEventRequest> clickBuffer = new EventQueueBuffer<>();
	private final EventQueueBuffer<PointerMoveEventRequest> moveBuffer = new EventQueueBuffer<>();
	private final EventQueueBuffer<PointerScrollEventRequest> scrollBuffer = new EventQueueBuffer<>();

	// add
	public void addClickEvent(PointerClickEventRequest event) {
		clickBuffer.addEvent(event.pageUrl(), event.memberSerialNumber(), event);
	}

	public void addMoveEvent(PointerMoveEventRequest event) {
		moveBuffer.addEvent(event.pageUrl(), event.memberSerialNumber(), event);
	}

	public void addScrollEvent(PointerScrollEventRequest event) {
		scrollBuffer.addEvent(event.pageUrl(), event.memberSerialNumber(), event);
	}

	// get
	public List<PointerClickEventRequest> getClickEvents(String pageUrl, String memberSerialNumber) {
		return clickBuffer.getEvents(pageUrl, memberSerialNumber);
	}

	public List<PointerMoveEventRequest> getMoveEvents(String pageUrl, String memberSerialNumber) {
		return moveBuffer.getEvents(pageUrl, memberSerialNumber);
	}

	public List<PointerScrollEventRequest> getScrollEvents(String pageUrl, String memberSerialNumber) {
		return scrollBuffer.getEvents(pageUrl, memberSerialNumber);
	}

	// flush
	public List<PointerClickEventRequest> flushClickEvents(String pageUrl, String memberSerialNumber) {
		return clickBuffer.flushEvents(pageUrl, memberSerialNumber);
	}

	public List<PointerMoveEventRequest> flushMoveEvents(String pageUrl, String memberSerialNumber) {
		return moveBuffer.flushEvents(pageUrl, memberSerialNumber);
	}

	public List<PointerScrollEventRequest> flushScrollEvents(String pageUrl, String memberSerialNumber) {
		return scrollBuffer.flushEvents(pageUrl, memberSerialNumber);
	}

	// 전체 클리어
	public void clearAll() {
		clickBuffer.clearAll();
		moveBuffer.clearAll();
		scrollBuffer.clearAll();
	}


}
