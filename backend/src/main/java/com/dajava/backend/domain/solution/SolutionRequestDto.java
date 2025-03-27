package com.dajava.backend.domain.solution;

import java.util.List;

public record SolutionRequestDto(
	String serialNumber,
	List<EventDataDto> eventData) {
	public record EventDataDto(
		String sessionId,
		long timestamp,
		String type,
		Integer x,
		Integer y,
		String element,
		String pageUrl,
		Integer broswerWidth
	) {}
}



