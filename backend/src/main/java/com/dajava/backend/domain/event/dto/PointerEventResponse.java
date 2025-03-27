package com.dajava.backend.domain.event.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PointerEventResponse(
	@NotNull String sessionId,
	@NotNull String pageUrl,
	@NotNull String type,
	Integer clientX,
	Integer clientY,
	Integer scrollY,
	Integer scrollHeight,
	Integer viewportHeight,
	String element,
	@NotNull LocalDateTime timestamp,
	@NotNull Integer browserWidth
) {
}
