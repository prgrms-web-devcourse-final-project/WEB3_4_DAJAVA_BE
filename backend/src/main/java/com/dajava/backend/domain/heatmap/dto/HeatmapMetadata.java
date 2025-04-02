package com.dajava.backend.domain.heatmap.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "히트맵 메타데이터")
public record HeatmapMetadata(
	@Schema(description = "최대 이벤트 발생 횟수", example = "25")
	int maxCount,

	@Schema(description = "전체 이벤트 수", example = "1250")
	int totalEvents,

	@Schema(description = "페이지 URL", example = "https://example.com/page")
	String pageUrl,

	@Schema(description = "총 세션 수", example = "3")
	int totalSessions,

	@Schema(description = "첫 이벤트 발생 시간")
	LocalDateTime firstEventTime,

	@Schema(description = "마지막 이벤트 발생 시간")
	LocalDateTime lastEventTime
) {
}
