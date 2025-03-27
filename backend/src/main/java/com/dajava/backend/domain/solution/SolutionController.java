package com.dajava.backend.domain.solution;

import static com.dajava.backend.domain.solution.SolutionUtils.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Gemini 솔루션 요청 컨트롤러 클래스
 * @author jhon S, sungkibum
 * @since 2025-03-24
 */
@Slf4j
@RequestMapping("/v1")
@Tag(name = "HomeController", description = "API 샘플 컨트롤러")
@RestController
@RequiredArgsConstructor
public class SolutionController {
	@Autowired
	private SolutionService solutionService;
	/**
	 * @param sessionDatas 사용자 세션 데이터를 포함한 리스트로, 각 세션에 대한 이벤트 데이터 및 관련 정보가 포함됩니다.
	 * @return {@link Mono<SolutionResponseDto>} AI 모델의 응답을 감싼 Mono 객체로, UI/UX 개선 솔루션을 담고 있습니다.
	 * @throws Exception AI 모델과의 통신 또는 데이터 처리 중 발생할 수 있는 예외가 있을 수 있습니다.
	 *
	 * @author jhon S, sungkibum
	 * @since 2025-03-24
	 */
	@PostMapping("/solution")
	@Operation(summary = "UX 개선 솔루션을 얻기 위한 API", description = "AI 모델에 로그 데이터를 보내 UX 개선 솔루션을 받아옵니다.")
	public Mono<SolutionResponseDto> getUXSolution(@RequestBody List<SolutionRequestDto> sessionDatas) {
		// 사용자 세션 데이터를 분석하여 UX 개선점을 제안하는 프롬프트 생성
		String prompt = refinePrompt(sessionDatas);
		// AI에게 전달할 데이터 생성
		String constructedRefineData = buildRefineData(prompt);
		// AI 솔루션 요청
		return solutionService.getAISolution(constructedRefineData);
	}


	@GetMapping("/solutioninfo/{serialNumber}/{password}")
	@Operation(summary = "최종 솔루션을 얻기 위한 API(Gemini 자연어 솔루션)", description = "최종 UI 개선 솔루션을 조회합니다.")
	public SolutionInfoResponse getSolutionInfo(@PathVariable String serialNumber, @PathVariable String password) {
		return solutionService.getSolutionInfo(serialNumber, password);
	}
}