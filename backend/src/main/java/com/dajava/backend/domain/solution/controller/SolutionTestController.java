package com.dajava.backend.domain.solution.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.solution.dto.SolutionInfoResponse;
import com.dajava.backend.domain.solution.scheduler.SolutionScheduler;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Gemini 솔루션 테스트용 코드 컨트롤러
 * @author NohDongHui
 * @since 2025-04-08
 */
@RestController
@RequestMapping("/v1/solution/test")
@RequiredArgsConstructor
@Slf4j
public class SolutionTestController {

	private final SolutionScheduler solutionScheduler;

	@GetMapping("/pushScheduler")
	@Operation(summary = "스케줄러 강제 푸쉬", description = "저장된 데이터를 토대로 강제로 ai솔루션을 반환하게 합니다..")
	public void getSolutionInfo() {
		solutionScheduler.processExpiredRegisters();
	}
}
