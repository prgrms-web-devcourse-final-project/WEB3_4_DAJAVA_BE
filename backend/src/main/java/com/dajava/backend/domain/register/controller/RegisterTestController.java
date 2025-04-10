package com.dajava.backend.domain.register.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterCreateResponse;
import com.dajava.backend.domain.register.service.RegisterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "RegisterController", description = "register 테스트용 api")
@RestController
@RequiredArgsConstructor
public class RegisterTestController {

	private final RegisterService registerService;

	@Operation(
		summary = "관리자 계정 SolutionCompleted값 false로 수정",
		description = "ai 솔루션 테스트를 위해 true인 SolutionCompleted값을 false로 수정합니다.")
	@PatchMapping("/v1/register/test/{serialNumber}")
	@ResponseStatus(HttpStatus.OK)
	public String create(
		@PathVariable String serialNumber
	) {
		registerService.modifyToFalseCompletedAdminSolution(serialNumber);
		return "관리자 계정 completedSolution false 변환 완료";
	}
}
