package com.dajava.backend.domain.solution;

import static com.dajava.backend.domain.solution.SolutionUtils.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.event.entity.SolutionData;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Gemini 솔루션 요청 컨트롤러
 * @author jhon S, sungkibum
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/v1/solution")
@RequiredArgsConstructor
public class SolutionController {
	private final SolutionService solutionService;

	@PostMapping
	@Operation(summary = "사용자 로그 기반 UX 개선 솔루션 요청", description = "사용자의 이벤트 로그 데이터를 AI 모델에 보내 UI/UX 개선 솔루션을 받아옵니다.")
	public Mono<SolutionResponseDto> getUXSolution(@RequestBody SolutionData sessionDatas) {
		String prompt = SolutionUtils.refinePrompt(sessionDatas);
		String constructedRefineData = SolutionUtils.buildRefineData(prompt);
		String serialNumber = extractSerialNumber(constructedRefineData);
		return solutionService.getAISolution(constructedRefineData, serialNumber);
	}

	@GetMapping("/info/{serialNumber}/{password}")
	@Operation(summary = "시리얼 번호 기반 UI 개선 솔루션 조회", description = "등록된 시리얼 번호와 비밀번호를 이용해 최종 UI 개선 솔루션을 조회합니다.")
	public SolutionInfoResponse getSolutionInfo(@PathVariable String serialNumber, @PathVariable String password) {
		return solutionService.getSolutionInfo(serialNumber, password);
	}

}