package com.dajava.backend.domain.event.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;

import io.swagger.v3.oas.annotations.Operation;

/**
 * EventLog 의 컨트롤러 입니다.
 * /v1/logs 로 들어온 마우스 이벤트 로그의 엔드포인트
 * @author Metronon
 */
@RestController
@RequestMapping("/v1/logs")
public class EventLogController {

	/**
	 * Click(Touch) 이벤트 로깅
	 * type 이 "click" 인 이벤트를 로깅합니다.
	 */
	@Operation(summary = "클릭 이벤트 로깅", description = "마우스 이벤트 클릭(터치) 타입의 이벤트를 로깅합니다.")
	@PostMapping("/click")
	@ResponseStatus(HttpStatus.OK)
	public String logClick(
		@RequestBody PointerClickEventRequest clickEventRequest
	) {
		return "클릭 이벤트 수신 완료";
	}

	/**
	 * mousemove 이벤트 로깅
	 * type 이 "mousemove"인 이벤트를 로깅합니다.
	 */
	@Operation(summary = "이동 이벤트 로깅", description = "마우스 이벤트 이동 타입의 이벤트를 로깅합니다.")
	@PostMapping("/movement")
	@ResponseStatus(HttpStatus.OK)
	public String logMovement(
		@RequestBody PointerMoveEventRequest moveEventRequest
	) {
		return "이동 이벤트 수신 완료";
	}

	/**
	 * scroll 이벤트 로깅
	 * type 이 "scroll"인 이벤트를 로깅합니다.
	 */
	@Operation(summary = "스크롤 이벤트 로깅", description = "마우스 이벤트 스크롤 타입의 이벤트를 로깅합니다.")
	@PostMapping("/scroll")
	@ResponseStatus(HttpStatus.OK)
	public String logScroll(
		@RequestBody PointerScrollEventRequest scrollEventRequest
	) {
		return "스크롤 이벤트 수신 완료";
	}
}
