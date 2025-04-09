package com.dajava.backend.redis.controller;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
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
	private final RedisService redisService;
	private final SessionDataDocumentRepository sessionDataDocumentRepository;
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
		redisService.createMoveEvent(moveEventRequest);
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
		redisService.createScrollEvent(scrollEventRequest);
		return "스크롤 이벤트 수신 완료";
	}

	@Operation(summary = "세션 시작 요청", description = "세션 시작 요청이 들어오면 해당 세션을 시작합니다.")
	@PostMapping("/start")
	@ResponseStatus(HttpStatus.OK)
	public void logStart(@RequestBody SessionDataKey sessionDataKey) {
		redisService.startSession(sessionDataKey);
	}


	@Operation(summary = "세션 종료 요청", description = "세션 종료 요청이 들어오면 해당 세션을 종료합니다.")
	@PostMapping("/end/{sessionId}")
	@ResponseStatus(HttpStatus.OK)
	public void logEnd(
		@PathVariable String sessionId
	) {
		redisService.expireSession(sessionId);
	}


	// 엘라스틱 서치 들어가는지 테스트
	@GetMapping("/api/test/session")
	public List<SessionDataDocument> getAllSessions() {
		List<SessionDataDocument> result = new ArrayList<>();
		sessionDataDocumentRepository.findAll().forEach(result::add);
		return result;
	}
}