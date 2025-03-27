package com.dajava.backend.domain.event.converter;

import com.dajava.backend.domain.event.PointerClickEvent;
import com.dajava.backend.domain.event.PointerMoveEvent;
import com.dajava.backend.domain.event.PointerScrollEvent;
import com.dajava.backend.domain.event.dto.PointerEventResponse;

public class PointerEventConverter {
	public static PointerEventResponse fromClickEvent(PointerClickEvent event) {
		return PointerEventResponse.builder()
			.sessionId(event.getSessionId())
			.pageUrl(event.getPageUrl())
			.type("click")
			.clientX(event.getClientX())
			.clientY(event.getClientY())
			// .element(event.getElement()) element 추가시 빌더 추가 예정
			.timestamp(event.getCreateDate())
			.browserWidth(event.getBrowserWidth())
			// .scrollHeight(event.getScrollHeight()) 현재 브라우저 스크롤 상태
			// .viewportHeight(event.getViewportHeight()) 현재 브라우저 height
			.build();
	}

	public static PointerEventResponse fromMoveEvent(PointerMoveEvent event) {
		return PointerEventResponse.builder()
			.sessionId(event.getSessionId())
			.pageUrl(event.getPageUrl())
			.type("mousemove")
			.clientX(event.getClientX())
			.clientY(event.getClientY())
			.timestamp(event.getCreateDate())
			.browserWidth(event.getBrowserWidth())
			// .scrollHeight(event.getScrollHeight()) 현재 브라우저 스크롤 상태
			// .viewportHeight(event.getViewportHeight()) 현재 브라우저 height
			.build();
	}

	public static PointerEventResponse fromScrollEvent(PointerScrollEvent event) {
		return PointerEventResponse.builder()
			.sessionId(event.getSessionId())
			.pageUrl(event.getPageUrl())
			.type("scroll")
			.scrollY(event.getScrollY())
			// .scrollHeight(event.getScrollHeight()) 현재 브라우저 스크롤 상태
			// .viewportHeight(event.getViewportHeight()) 현재 브라우저 height
			.timestamp(event.getCreateDate())
			.browserWidth(event.getBrowserWidth())
			.build();
	}
}
