package com.dajava.backend.domain.solution;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.MediaType;

import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
	private final SolutionService solutionService;
	/**
	 * UX 개선 솔루션을 얻기 위한 API (Mono 반환)
	 *
	 * @param sessionData 사용자 세션 로그 데이터
	 * @return AI 모델이 분석한 UX 개선 솔루션 (Mono<String>)
	 */
	@PostMapping(value = "/solutions/json", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "UX 개선 솔루션을 얻기 위한 API", description = "AI 모델에 로그 데이터를 보내 UX 개선 솔루션을 받아옵니다.")

	public Mono<SolutionResponseDto> getUXSolution(@RequestBody List<Map<String, Object>> sessionData) {
		// Todo.. DTO 화
		String prompt = String.format("다음 사용자 세션 데이터를 분석하여 UI/UX 개선점을 제안해주세요: %s", sessionData);
		String constructedRefineData = String.format("{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}", prompt);
		log.info("Generated prompt: {}", prompt);
		log.info("Received sessionData: {}", sessionData);
		log.info("Constructed refineData: {}", constructedRefineData);
		return solutionService.getAISolutions(constructedRefineData);
	}
}
