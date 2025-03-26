package com.dajava.backend.domain.event.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Scroll(Drag) 관련 요청 DTO 입니다.
 * @author Metronon
 */
public record PointerScrollEventRequest(
	@NotNull String sessionId,
	@NotNull String pageUrl,
	@NotNull String memberSerialNumber,
	@NotNull Long timestamp,
	@NotNull Integer browserWidth,
	@NotNull Integer scrollX,
	@NotNull Integer scrollY
) {
}
