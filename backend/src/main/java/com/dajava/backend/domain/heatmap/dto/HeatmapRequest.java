package com.dajava.backend.domain.heatmap.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "히트맵 요청 DTO")
public record HeatmapRequest(
	@Schema(description = "이벤트 타입", example = "click", allowableValues = {"click", "move", "scroll"})
	String type,

	@Schema(description = "시작 시간", example = "2025-04-02T09:00:00")
	LocalDateTime startTime,

	@Schema(description = "종료 시간", example = "2025-04-01T23:59:59")
	LocalDateTime endTime
) {
}
