package com.dajava.backend.domain.event.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Scroll(Drag) 관련 요청 DTO 입니다.
 * @author Metronon
 */
public record PointerScrollEventRequest(
	@NotNull Long sessionId,
	@NotNull String pageUrl,
	@NotNull String memberSerialNumber,
	@NotNull Long timestamp,
	@NotNull int browserWidth,
	@NotNull Integer scrollX,
	@NotNull Integer scrollY
) {
}
