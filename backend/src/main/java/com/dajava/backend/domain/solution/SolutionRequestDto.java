package com.dajava.backend.domain.solution;

import java.util.List;

import lombok.Data;


public record SolutionRequestDto(
	String userId,
	String sessionId,
	String device,
	String browser,
	List<MouseLogDto> mouseLogs,
	List<ScrollDataDto> scrollData,
	List<ClickDataDto> clickData,
	List<PageVisitDto> pageVisits) {
	public record MouseLogDto(
		long timestamp,
		String type,
		int x,
		int y,
		String element
	) {}

	public record ScrollDataDto(
		long timestamp,
		int scrollTop,
		int scrollHeight,
		int viewportHeight
	) {}

	public record ClickDataDto(
		long timestamp,
		int x,
		int y,
		String element
	) {}

	public record PageVisitDto(
		long timestamp,
		String url
	) {}
}



