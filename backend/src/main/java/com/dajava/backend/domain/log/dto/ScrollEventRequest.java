package com.dajava.backend.domain.log.dto;

import com.dajava.backend.domain.log.dto.base.BaseEventRequest;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScrollEventRequest extends BaseEventRequest {

	@Schema(description = "이동시 X 좌표값", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer clientX;

	@Schema(description = "이동시 Y 좌표값", example = "500", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer clientY;

	@NotNull
	private Integer scrollY;

	@NotNull
	private String element;

	@Builder
	public ScrollEventRequest(
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
