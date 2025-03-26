package com.dajava.backend.domain.solution;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.MediaType;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Gemini 솔루션 요청 컨트롤러 클래스
 * @author jhon S, sungkibum
 * @since 2025-03-24
 */
@RequestMapping("/v1")
@Tag(name = "HomeController", description = "API 샘플 컨트롤러")
@RestController
@RequiredArgsConstructor
@Slf4j
public class SolutionController {
	@Autowired
	private SolutionService solutionService;

	/**
	 * UX 개선 솔루션을 얻기 위한 API
	 * @param sessionDatas
	 * @return result(response.block ())
	 * @author jhon S, sungkibum
	 * @since 2025-03-24
	 */
	@PostMapping("/solution")
	@Operation(summary = "UX 개선 솔루션을 얻기 위한 API", description = "AI 모델에 로그 데이터를 보내 UX 개선 솔루션을 받아옵니다.")
	public Mono<SolutionResponseDto> getUXSolution(@RequestBody List<SolutionRequestDto> sessionDatas) {
		log.info("Received sessionData: " + sessionDatas.toString());
		String prompt = String.format("다음 사용자 세션 데이터를 분석하여 UI/UX 개선점을 제안해주세요: %s", sessionDatas);
		log.info("Generated prompt: " + prompt);
		String constructedRefineData = String.format("{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}", prompt);
		log.info("Constructed refineData: " + constructedRefineData);
		return solutionService.getAISolution(constructedRefineData);
	}

	@PostMapping(value = "/solutions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Operation(summary = "UX 개선 솔루션을 얻기 위한 API", description = "AI 모델에 로그 데이터를 보내 UX 개선 솔루션을 받아옵니다.")
	public Flux<String> getUXSolutions(@RequestBody List<Map<String, Object>> sessionData) {
		log.info("Received sessionData: " + sessionData.toString());
		String prompt = String.format("다음 사용자 세션 데이터를 분석하여 UI/UX 개선점을 제안해주세요: %s", sessionData);
		log.info("Generated prompt: " + prompt);
		String constructedRefineData = String.format("{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}", prompt);
		log.info("Constructed refineData: " + constructedRefineData);
		return solutionService.getAISolutions(constructedRefineData);
	}

	@GetMapping("/solution/{serialNumber}/{password}")
	@Operation(summary = "최종 솔루션을 얻기 위한 API(Gemini 자연어 솔루션)", description = "최종 UI 개선 솔루션을 조회합니다.")
	public SolutionInfoResponse getSolution(@PathVariable String serialNumber, @PathVariable String password) {
		return solutionService.getSolution(serialNumber, password);
	}
}