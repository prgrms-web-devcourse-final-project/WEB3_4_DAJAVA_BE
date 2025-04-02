package com.dajava.backend.domain.heatmap.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "히트맵 응답 DTO")
public record HeatmapResponse(
	@Schema(description = "그리드 크기 (픽셀 단위, 현재는 10으로 고정)", example = "10")
	int gridSize,

	@Schema(description = "페이지 전체 너비", example = "1200")
	int pageWidth,

	@Schema(description = "페이지 전체 높이", example = "3000")
	int pageHeight,

	@Schema(description = "히트맵 그리드 셀 데이터")
	List<GridCell> gridCells,

	@Schema(description = "히트맵 메타데이터")
	HeatmapMetadata metadata
) {
}
