package com.dajava.backend.domain.event.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.NotNull;

/**
 * Click(Touch) 관련 요청 DTO 입니다.
 * @author Metronon
 */
public record PointerClickEventRequest(
	@NotNull String sessionId,
	@NotNull String pageUrl,
	@NotNull String memberSerialNumber,
	@NotNull Long timestamp,
	@NotNull int browserWidth,
	@JsonAlias("x") @NotNull Integer clientX,
	@JsonAlias("y") @NotNull Integer clientY
) {
}
