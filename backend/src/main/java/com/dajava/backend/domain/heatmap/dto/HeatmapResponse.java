package com.dajava.backend.domain.heatmap.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "히트맵 응답 DTO")
public record HeatmapResponse(
	@Schema(description = "그리드 단위 (현재는 10px 값으로 고정해서 히트맵 데이터 생성)", example = "10")
	int gridSize,

	@Schema(description = "X 그리드 갯수 (pageWidth 를 그리드 사이즈로 나눈 값)", example = "192")
	int gridSizeX,

	@Schema(description = "Y 그리드 갯수 (pageWidth 를 그리드 사이즈로 나눈 값)", example = "250")
	int gridSizeY,

	@Schema(description = "페이지 전체 너비", example = "1200")
	int pageWidth,

	@Schema(description = "페이지 전체 높이", example = "3000")
	int pageHeight,

	@Schema(description = "전체 페이지 캡쳐 이미지 파일명", example = "91710d82-fb14-4c7c-aed6-761fa2db02f8.png")
	String pageCapture,

	@Schema(description = "히트맵 그리드 셀 데이터")
	List<GridCell> gridCells,

	@Schema(description = "히트맵 메타데이터")
	HeatmapMetadata metadata
) {
}
