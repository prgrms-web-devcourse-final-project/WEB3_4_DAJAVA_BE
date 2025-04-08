package com.dajava.backend.domain.event.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.es.scheduler.vaildation.EsEventValidateScheduler;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/logs")
@RequiredArgsConstructor
public class EventLogTestController {

	private final EsEventValidateScheduler esEventValidateScheduler;

	@Operation(summary = "검증 스케줄러 강제 푸쉬", description = "검증 스케줄러를 강제로 푸쉬합니다.")
	@GetMapping("/test/push/validateScheduler")
	@ResponseStatus(HttpStatus.OK)
	public String logClick() {
		esEventValidateScheduler.endedSessionValidate();
		return "검증 스케줄러 동작 완료";
	}
}
