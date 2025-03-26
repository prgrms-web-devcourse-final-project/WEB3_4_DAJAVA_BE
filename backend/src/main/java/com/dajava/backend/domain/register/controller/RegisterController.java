package com.dajava.backend.domain.register.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.register.dto.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.RegisterCreateResponse;
import com.dajava.backend.domain.register.service.RegisterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RegisterController
 * "/v1/solution" 로 들어오는 신청 관련 API 컨트롤러
 * 신청관련 요청을 처리
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Slf4j
@RequestMapping(value = "/v1/register", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "RegisterController", description = "API 신청 폼 컨트롤러")
@RestController
@RequiredArgsConstructor
public class RegisterController {

	private final RegisterService registerService;

	/**
	 * 솔루션 신청 폼 접수 API
	 * @param request 신청 데이터 (RegisterCreateRequest)
	 * @return 신청 결과 (RegisterCreateResponse)
	 */
	@Operation(
		summary = "솔루션 요청",
		description = "솔루션 폼 정보를 기반으로 등록 후 일련 번호 등 등록 정보를 반환합니다.")
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public RegisterCreateResponse create(
		@RequestBody RegisterCreateRequest request
	) {
		log.info(request.toString());
		return registerService.createRegister(request);
	}
}
