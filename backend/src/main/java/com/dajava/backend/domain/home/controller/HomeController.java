package com.dajava.backend.domain.home.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.home.dto.SampleRequest;
import com.dajava.backend.domain.home.dto.SampleResponse;
import com.dajava.backend.domain.home.service.SampleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HomeController
 * "/v1" 로 들어오는 요청 처리 컨트롤러
 * 프로젝트를 시작하기 전, 참고하는 용도로 사용하기 위해 작성
 *
 * @author ChoiHyunSan
 * @since 2025-03-22
 */
@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class HomeController {

	private final SampleService sampleService;

	/**
	 * SampleController 에 작성된 예시 컨트롤러
	 * @param request Sample 로직에 필요한 요청 정보
	 * @return SampleResponse (DTO)
	 */
	@GetMapping("/")
	public SampleResponse home(
		@RequestBody SampleRequest request
	){
		SampleResponse response = sampleService.sampleLogic(request);
		log.info("Response Data : {}", request.toString());
		return response;
	}
}
