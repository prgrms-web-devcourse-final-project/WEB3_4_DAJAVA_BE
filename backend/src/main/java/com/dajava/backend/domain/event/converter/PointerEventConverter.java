package com.dajava.backend.domain.event.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.entity.PointerClickEvent;
import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;
import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;

public class PointerEventConverter {

	//utiltiy class에서 생성자 호출되는 경우 막음
	private PointerEventConverter() {
	}

	public static SolutionEvent fromClick(PointerClickEvent event, SolutionData solutionData) {
		return SolutionEvent.builder()
			.sessionId(event.getSessionId())
			.pageUrl(event.getPageUrl())
			.type("click")
			.clientX(event.getClientX())
			.clientY(event.getClientY())
			.timestamp(event.getCreateDate())
			.browserWidth(event.getBrowserWidth())
			.solutionData(solutionData)
			.element(event.getElement())
			.scrollY(event.getScrollY())
			.scrollHeight(event.getScrollHeight())
			.viewportHeight(event.getViewportHeight())
			.build();

	}

	public static SolutionEvent fromMove(PointerMoveEvent event, SolutionData solutionData) {
		return SolutionEvent.builder()
			.sessionId(event.getSessionId())
			.pageUrl(event.getPageUrl())
			.type("mousemove")
			.clientX(event.getClientX())
			.clientY(event.getClientY())
			.timestamp(event.getCreateDate())
			.browserWidth(event.getBrowserWidth())
			.solutionData(solutionData)
			.scrollY(event.getScrollY())
			.scrollHeight(event.getScrollHeight())
			.viewportHeight(event.getViewportHeight())
			// .tag(event.getTag()) tag 추가시 빌더 추가 예정
			.build();
	}

	public static SolutionEvent fromScroll(PointerScrollEvent event, SolutionData solutionData) {
		return SolutionEvent.builder()
			.sessionId(event.getSessionId())
			.pageUrl(event.getPageUrl())
			.type("scroll")
			.scrollY(event.getScrollY())
			.timestamp(event.getCreateDate())
			.browserWidth(event.getBrowserWidth())
			.solutionData(solutionData)
			.scrollHeight(event.getScrollHeight())
			.viewportHeight(event.getViewportHeight())
			.build();
	}

	public static List<SolutionEvent> toSolutionEvents(
		List<PointerClickEvent> clicks,
		List<PointerMoveEvent> moves,
		List<PointerScrollEvent> scrolls,
		SolutionData solutionData
	) {
		List<SolutionEvent> result = new ArrayList<>();

		result.addAll(clicks.stream()
			.map(click -> PointerEventConverter.fromClick(click, solutionData))
			.toList());

		result.addAll(moves.stream()
			.map(move -> PointerEventConverter.fromMove(move, solutionData))
			.toList());

		result.addAll(scrolls.stream()
			.map(scroll -> PointerEventConverter.fromScroll(scroll, solutionData))
			.toList());

		return result;
	}

	public static PointerClickEventDocument toClickEventDocument(PointerClickEventRequest request) {
		return PointerClickEventDocument.builder()
			.id(UUID.randomUUID().toString())
			.sessionId(request.sessionId())
			.pageUrl(request.pageUrl())
			.memberSerialNumber(request.memberSerialNumber())
			.timestamp(request.timestamp())
			.browserWidth(request.browserWidth())
			.clientX(request.clientX())
			.clientY(request.clientY())
			.scrollY(request.scrollY())
			.scrollHeight(request.scrollHeight())
			.viewportHeight(request.viewportHeight())
			.element(request.element())
			.build();
	}

	public static PointerMoveEventDocument toMoveEventDocument(PointerMoveEventRequest request) {
		return PointerMoveEventDocument.builder()
			.id(UUID.randomUUID().toString())
			.sessionId(request.sessionId())
			.pageUrl(request.pageUrl())
			.memberSerialNumber(request.memberSerialNumber())
			.timestamp(request.timestamp())
			.browserWidth(request.browserWidth())
			.clientX(request.clientX())
			.clientY(request.clientY())
			.scrollY(request.scrollY())
			.scrollHeight(request.scrollHeight())
			.viewportHeight(request.viewportHeight())
			.build();
	}

	public static PointerScrollEventDocument toScrollEventDocument(PointerScrollEventRequest request) {
		return PointerScrollEventDocument.builder()
			.id(UUID.randomUUID().toString())
			.sessionId(request.sessionId())
			.pageUrl(request.pageUrl())
			.memberSerialNumber(request.memberSerialNumber())
			.timestamp(request.timestamp())
			.browserWidth(request.browserWidth())
			.scrollY(request.scrollY())
			.scrollHeight(request.scrollHeight())
			.viewportHeight(request.viewportHeight())
			.build();
	}
}


