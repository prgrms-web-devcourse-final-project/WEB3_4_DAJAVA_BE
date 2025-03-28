package com.dajava.backend.domain.event.converter;

import java.util.ArrayList;
import java.util.List;

import com.dajava.backend.domain.event.entity.PointerClickEvent;
import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;

public class PointerEventConverter {
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
			// .element(event.getElement()) element 추가시 빌더 추가 예정
			// .scrollHeight(event.getScrollHeight()) 현재 브라우저 스크롤 상태
			// .viewportHeight(event.getViewportHeight()) 현재 브라우저 height
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
			// .element(event.getElement()) element 추가시 빌더 추가 예정
			// .scrollHeight(event.getScrollHeight()) 현재 브라우저 스크롤 상태
			// .viewportHeight(event.getViewportHeight()) 현재 브라우저 height
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
			// .element(event.getElement()) element 추가시 빌더 추가 예정
			// .scrollHeight(event.getScrollHeight()) 현재 브라우저 스크롤 상태
			// .viewportHeight(event.getViewportHeight()) 현재 브라우저 height
			.build();
	}

	public static List<SolutionEvent> toPointerEvents(
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
}

