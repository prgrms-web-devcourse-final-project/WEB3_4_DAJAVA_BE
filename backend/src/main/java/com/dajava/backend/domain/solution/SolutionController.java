package com.dajava.backend.domain.solution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequestMapping("/v1")
@Tag(name = "HomeController", description = "API 샘플 컨트롤러")
@RestController
@RequiredArgsConstructor
public class SolutionController {
	@Autowired
	private SolutionService solutionService;

	@PostMapping("/ux-solution")
	@Operation(summary = "UX 개선 솔루션을 얻기 위한 API", description = "AI 모델에 로그 데이터를 보내 UX 개선 솔루션을 받아옵니다.")
	public String getUXSolution(String dummy) {
		return solutionService.getAISolution(dummy);
	}
}