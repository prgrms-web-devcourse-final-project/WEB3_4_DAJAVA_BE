package com.dajava.backend.domain.solution.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;


public record SolutionRequestDto(

	String serialNumber,

	List<EventDataDto> eventData) {

	public record EventDataDto(

		String sessionId,

		LocalDateTime timestamp,

		Integer clientX,
		 Integer clientY,
	Integer scrollY,
			Integer scrollHeight,
			 Integer viewportHeight,
	String element,

	) {}




	public static SolutionRequestDto from(SolutionData solutionData) {

		List<EventDataDto> eventDataDtos = solutionData.getSolutionEvents().stream()

			.map(SolutionRequestDto::convertToEventDataDto)

			.collect(Collectors.toList());



		return new SolutionRequestDto(solutionData.getSerialNumber(), eventDataDtos);

	}



	private static EventDataDto convertToEventDataDto(SolutionEvent event) {

		return new EventDataDto(

			event.getSessionId(),

			event.getTimestamp(),

			event.getType(),

			event.getClientX(),

			event.getClientY(),

			event.getElement(),

			event.getPageUrl(),

			event.getBrowserWidth()

		);

	}

}
