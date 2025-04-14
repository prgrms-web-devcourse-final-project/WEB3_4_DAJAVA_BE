package com.dajava.backend.domain.solution.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.register.dto.register.RegisterCheckRequest;
import com.dajava.backend.domain.register.dto.register.RegisterCheckResponse;
import com.dajava.backend.domain.solution.dto.SolutionInfoResponse;
import com.dajava.backend.domain.solution.dto.SolutionRequest;
import com.dajava.backend.domain.solution.dto.SolutionResponse;
import com.dajava.backend.domain.solution.service.SolutionService;
import com.dajava.backend.domain.solution.utils.SolutionUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Gemini 솔루션 요청 컨트롤러
 * @author jhon S, sungkibum
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/v1/solution")
@RequiredArgsConstructor
@Slf4j
public class SolutionController {
	private final SolutionService solutionService;

	@PostMapping
	@Operation(summary = "사용자 로그 기반 UX 개선 솔루션 요청", description = "사용자의 이벤트 로그 데이터를 AI 모델에 보내 UI/UX 개선 솔루션을 받아옵니다.")
	public Mono<SolutionResponse> getUXSolution(@RequestBody SolutionRequest solutionRequest) {
		// serialNumber 추출
		String serialNumber = SolutionUtils.extractsSerialNumber(solutionRequest);
		// 이벤트 로그 데이터 추출
		List<SolutionRequest.EventDataDto> eventDataDto = SolutionUtils.extractSolutionEvents(solutionRequest);
		// 이벤트 로그 데이터와 질문을 합쳐 스트링화
		String prompt = SolutionUtils.refinePrompt(eventDataDto);
		// AI 요구 구조로 parsing
		String constructedRefineData = SolutionUtils.buildRefineData(prompt);
		return solutionService.getAISolution(constructedRefineData, serialNumber);
	}

	@GetMapping("/info/{serialNumber}/{password}")
	@Operation(summary = "시리얼 번호 기반 UI 개선 솔루션 조회", description = "등록된 시리얼 번호와 비밀번호를 이용해 최종 UI 개선 솔루션을 조회합니다.")
	public SolutionInfoResponse getSolutionInfo(@PathVariable String serialNumber, @PathVariable String password) {
		return solutionService.getSolutionInfo(serialNumber, password);
	}
}