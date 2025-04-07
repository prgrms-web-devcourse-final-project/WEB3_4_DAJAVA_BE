package com.dajava.backend.redis.controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.service.EventLogService;
import com.dajava.backend.redis.service.RedisService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * EventLog 의 컨트롤러 입니다.
 * /v1/logs 로 들어온 마우스 이벤트 로그의 엔드포인트
 * @author Metronon
 */
@RestController
@RequestMapping("/v1/logs/redis")
@RequiredArgsConstructor
@Tag(name = "EventLogController", description = "이벤트 로깅 컨트롤러")
public class EventLogRedisController {
	private final EventLogService eventLogService;
	private final RedisService redisService;

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
		redisService.createClickEvent(clickEventRequest);
		return "클릭 이벤트 수신 완료";
	}
}