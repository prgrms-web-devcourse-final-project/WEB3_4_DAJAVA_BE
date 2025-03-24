package com.dajava.backend.domain.solution.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.solution.dto.SolutionCreateRequest;
import com.dajava.backend.domain.solution.dto.SolutionCreateResponse;
import com.dajava.backend.domain.solution.service.SolutionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SolutionController
 * "/v1/solution" 로 들어오는 API 컨트롤러
 * 신청관련 요청을 처리
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Slf4j
@RequestMapping("/v1/solution")
@Tag(name = "SolutionController", description = "API 신청 폼 컨트롤러")
@RestController
@RequiredArgsConstructor
public class SolutionController {

	private final SolutionService solutionService;

	/**
	 * 솔루션 신청 폼 접수 API
	 * @param request 신청 데이터 (SolutionCreateRequest)
	 * @return 신청 결과 (SolutionCreateResponse)
	 */
	@Operation(
		summary = "솔루션 요청",
		description = "솔루션 폼 정보를 기반으로 등록 후 일련 번호 등 등록 정보를 반환합니다.")
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public SolutionCreateResponse create(
		SolutionCreateRequest request
	) {
		log.info(request.toString());
		return solutionService.createSolution(request);
	}
}
