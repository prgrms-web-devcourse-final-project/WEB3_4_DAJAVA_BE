package com.dajava.backend.domain.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 이동 이벤트 요청 DTO 입니다.
 */
public class MovementEventRequest extends BaseEventRequest {
	@Schema(description = "이동시 X 좌표값", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer clientX;

	@Schema(description = "이동시 Y 좌표값", example = "500", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer clientY;

	public Integer getClientX() {
		return clientX;
	}
	public Integer getClientY() {
		return clientY;
	}
}
