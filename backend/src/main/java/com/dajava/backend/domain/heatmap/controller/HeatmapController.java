package com.dajava.backend.domain.heatmap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.heatmap.dto.HeatmapResponse;
import com.dajava.backend.domain.heatmap.service.HeatmapService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HeatmapController
 * 히트맵과 관련된 API 요청 컨트롤러입니다.
 * @author Metronon
 * @since 2025-04-02
 */
@RestController
@RequestMapping("/v1/solution")
@RequiredArgsConstructor
@Slf4j
public class HeatmapController {

	private final HeatmapService heatmapService;

	@GetMapping("/heatmap/{serialNumber}/{password}")
	@Operation(
		summary = "시리얼 번호 기반 전체 히트맵 조회",
		description = "등록된 시리얼 번호와 비밀번호를 이용해 각 이벤트 타입의 히트맵을 조회합니다.")
	public HeatmapResponse getHeatmap(
		@PathVariable String serialNumber,
		@PathVariable String password,
		@RequestParam String type
	) {
		return heatmapService.getHeatmap(serialNumber, password, type);
	}
}
