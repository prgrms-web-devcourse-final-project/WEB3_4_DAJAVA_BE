package com.dajava.backend.domain.event.converter;

import java.nio.charset.StandardCharsets;
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
import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;
import com.dajava.backend.global.utils.TimeUtils;

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
			.id(request.eventId() + request.timestamp())
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
			.isOutlier(false)
			.build();
	}

	public static PointerMoveEventDocument toMoveEventDocument(PointerMoveEventRequest request) {
		return PointerMoveEventDocument.builder()
			.id(request.eventId() + request.timestamp())
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
			.isOutlier(false)
			.build();
	}

	public static PointerScrollEventDocument toScrollEventDocument(PointerScrollEventRequest request) {
		return PointerScrollEventDocument.builder()
			.id(request.eventId() + request.timestamp())
			.sessionId(request.sessionId())
			.pageUrl(request.pageUrl())
			.memberSerialNumber(request.memberSerialNumber())
			.timestamp(request.timestamp())
			.browserWidth(request.browserWidth())
			.scrollY(request.scrollY())
			.scrollHeight(request.scrollHeight())
			.viewportHeight(request.viewportHeight())
			.isOutlier(false)
			.build();
	}

	public static SolutionEventDocument fromClickDocument(PointerClickEventDocument event) {

		String raw = event.getSessionId() + "|" + event.getPageUrl() +  "|" + event.getTimestamp();
		String id = UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8)).toString();

		return SolutionEventDocument.builder()
			.id(id)
			.sessionId(event.getSessionId())
			.pageUrl(event.getPageUrl())
			.serialNumber(event.getMemberSerialNumber())
			.type("click")
			.clientX(event.getClientX())
			.clientY(event.getClientY())
			.timestamp(TimeUtils.toEpochMillis(event.getTimestamp()))
			.browserWidth(event.getBrowserWidth())
			.scrollY(event.getScrollY())
			.scrollHeight(event.getScrollHeight())
			.viewportHeight(event.getViewportHeight())
			.element(event.getElement()) // element를 tag로 쓸 경우
			.build();
	}

	public static SolutionEventDocument fromMoveDocument(PointerMoveEventDocument event) {

		String raw = event.getSessionId() + "|" + event.getPageUrl() + "|" + event.getTimestamp();
		String id = UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8)).toString();

		return SolutionEventDocument.builder()
			.id(id)
			.sessionId(event.getSessionId())
			.pageUrl(event.getPageUrl())
			.serialNumber(event.getMemberSerialNumber())
			.type("move")
			.clientX(event.getClientX())
			.clientY(event.getClientY())
			.timestamp(TimeUtils.toEpochMillis(event.getTimestamp()))
			.browserWidth(event.getBrowserWidth())
			.scrollY(event.getScrollY())
			.scrollHeight(event.getScrollHeight())
			.viewportHeight(event.getViewportHeight())
			.isOutlier(event.getIsOutlier())
			.build();
	}

	public static SolutionEventDocument fromScrollDocument(PointerScrollEventDocument event) {

		String raw = event.getSessionId() + "|" + event.getPageUrl() + "|" + event.getTimestamp();
		String id = UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8)).toString();
		return SolutionEventDocument.builder()
			.id(id)
			.sessionId(event.getSessionId())
			.pageUrl(event.getPageUrl())
			.serialNumber(event.getMemberSerialNumber())
			.type("scroll")
			.clientX(null) // scroll 이벤트에는 X/Y 좌표가 없을 수도 있음
			.clientY(null)
			.timestamp(TimeUtils.toEpochMillis(event.getTimestamp()))
			.browserWidth(event.getBrowserWidth())
			.scrollY(event.getScrollY())
			.scrollHeight(event.getScrollHeight())
			.viewportHeight(event.getViewportHeight())
			.isOutlier(event.getIsOutlier())
			.build();
	}

	public static List<SolutionEventDocument> toSolutionEventDocuments(
		List<PointerClickEventDocument> clicks,
		List<PointerMoveEventDocument> moves,
		List<PointerScrollEventDocument> scrolls
	) {
		List<SolutionEventDocument> result = new ArrayList<>();

		result.addAll(clicks.stream()
			.map(PointerEventConverter::fromClickDocument)
			.toList());

		result.addAll(moves.stream()
			.map(PointerEventConverter::fromMoveDocument)
			.toList());

		result.addAll(scrolls.stream()
			.map(PointerEventConverter::fromScrollDocument)
			.toList());

		return result;
	}
}


