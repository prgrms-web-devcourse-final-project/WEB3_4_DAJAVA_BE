package com.dajava.backend.domain.log.dto;

import com.dajava.backend.domain.log.dto.base.BaseEventRequest;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClickEventRequest extends BaseEventRequest {

	@NotNull
	private Integer clientX;

	@NotNull
	private Integer clientY;

	@NotNull
	private Integer scrollY;

	@NotNull
	private String element;

	@Builder
	public ClickEventRequest(
		String eventId,
		Long timestamp,
		Integer browserWidth,
		Integer scrollHeight,
		Integer viewportHeight,
		SessionIdentifier sessionIdentifier,
		Integer clientX,
		Integer clientY,
		Integer scrollY,
		String element
	) {
		super(eventId, timestamp, browserWidth, scrollHeight, viewportHeight, sessionIdentifier);
		this.clientX = clientX;
		this.clientY = clientY;
		this.scrollY = scrollY;
		this.element = element;
	}

}
