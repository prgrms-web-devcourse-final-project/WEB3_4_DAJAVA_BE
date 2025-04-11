package com.dajava.backend.domain.log.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;
import com.dajava.backend.domain.log.scheduler.SessionScheduler;
import com.dajava.backend.domain.log.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 이벤트 수집 컨트롤러 입니다.
 * @author Metronon
 */
@RestController
@RequestMapping("/v1/logs")
@RequiredArgsConstructor
@Tag(name = "EventLogController", description = "이벤트 로깅 컨트롤러")
public class EventController {
	private final EventService eventService;
	private final SessionScheduler sessionScheduler;

	/**
	 * type 이 "click" 인 Click(Touch) 이벤트가 redis에 저장
	 */
	@Operation(summary = "클릭 이벤트 로깅", description = "마우스 이벤트 클릭(터치) 타입의 이벤트를 로깅합니다.")
	@PostMapping("/click")
	@ResponseStatus(HttpStatus.OK)
	public String ClickEvent(
		@RequestBody ClickEventRequest clickEventRequest
	) {
		eventService.createClickEvent(clickEventRequest);
		return "클릭 이벤트 수신 완료";
	}

	/**
	 * type 이 "mousemove"인 mousemove 이벤트가 redis에 저장
	 */
	@Operation(summary = "이동 이벤트 로깅", description = "마우스 이벤트 이동 타입의 이벤트를 로깅합니다.")
	@PostMapping("/movement")
	@ResponseStatus(HttpStatus.OK)
	public String MovementEvent(
		@RequestBody MovementEventRequest movementEventRequest
	) {

		eventService.createMoveEvent(movementEventRequest);
		return "이동 이벤트 수신 완료";
	}

	/**
	 * type 이 "scroll"인 scroll 이벤트가 redis에 저장
	 */
	@Operation(summary = "스크롤 이벤트 로깅", description = "마우스 이벤트 스크롤 타입의 이벤트를 로깅합니다.")
	@PostMapping("/scroll")
	@ResponseStatus(HttpStatus.OK)
	public String ScrollEvent(
		@RequestBody ScrollEventRequest scrollEventRequest
	) {
		eventService.createScrollEvent(scrollEventRequest);
		return "스크롤 이벤트 수신 완료";
	}
}