package com.dajava.backend.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotNull;

/**
 * Move 관련 요청 DTO 입니다.
 * @author Metronon
 */
public record PointerMoveEventRequest(
	@NotNull String sessionId,
	@NotNull String pageUrl,
	@NotNull String memberSerialNumber,
	@NotNull Long timestamp,
	@NotNull Integer browserWidth,
	@JsonAlias("x") @NotNull Integer clientX,
	@JsonAlias("y") @NotNull Integer clientY
) {
}
