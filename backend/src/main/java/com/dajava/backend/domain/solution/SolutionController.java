package com.dajava.backend.domain.solution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Gemini 솔루션 요청 컨트롤러 클래스
 * @author jhon S, sungkibum
 * @since 2025-03-24
 */
@RequestMapping("/v1")
@Tag(name = "HomeController", description = "API 샘플 컨트롤러")
@RestController
@RequiredArgsConstructor
public class SolutionController {
	@Autowired
	private SolutionService solutionService;

	/**
	 * UX 개선 솔루션을 얻기 위한 API
	 * @param refineData
	 * @return result(response.block())
	 * @author jhon S, sungkibum
	 * @since 2025-03-24
	 */
	@PostMapping("/ux-solution")
	@Operation(summary = "UX 개선 솔루션을 얻기 위한 API", description = "AI 모델에 로그 데이터를 보내 UX 개선 솔루션을 받아옵니다.")
	public String getUXSolution(String refineData) {
		return solutionService.getAISolution(refineData);
	}

	@PostMapping(value = "/ux-solutions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Operation(summary = "UX 개선 솔루션을 얻기 위한 API", description = "AI 모델에 로그 데이터를 보내 UX 개선 솔루션을 받아옵니다.")
	public Flux<String> getUXSolutions(@RequestBody String refineData) {
		return solutionService.getAISolutions(refineData);
	}
}