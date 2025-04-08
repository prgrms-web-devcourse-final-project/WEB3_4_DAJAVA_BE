// package com.dajava.backend.domain.event.service;
//
// import java.util.Set;
//
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
// import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
// import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
// import com.dajava.backend.domain.event.dto.SessionDataKey;
// import com.dajava.backend.domain.event.entity.SessionData;
// import com.dajava.backend.domain.event.repository.SessionDataRepository;
// import com.dajava.backend.global.utils.SessionDataKeyUtils;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class EventLogServiceByRedisImpl implements EventLogService {
// 	private final SessionDataRepository sessionDataRepository;
// 	private final SessionDataService sessionDataService;
// 	private final ActivityHandleService activityHandleService;
// 	private final RedisTemplate<String, String> redisTemplate;
// 	private final ObjectMapper objectMapper;
//
// 	private static final String EVENT_CACHE_PREFIX = "event:";
// 	private static final String ACTIVE_SESSION_KEYS_SET = "active_sessions"; // 활성 세션 관리를 위한 Redis Set
//
// 	/**
// 	 * 클릭 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, Redis 에 캐싱합니다.
// 	 */
// 	@Override
// 	@Transactional
// 	public void createClickEvent(PointerClickEventRequest request) {
// 		log.info("클릭 이벤트 로깅 및 Redis 캐싱: {}", request);
//
// 		SessionDataKey sessionDataKey = new SessionDataKey(
// 			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
// 		);
//
// 		sessionDataService.createOrFindSessionData(sessionDataKey); // SessionData 존재 확인 또는 생성
//
// 		try {
// 			String eventType = "click";
// 			String eventDataJson = objectMapper.writeValueAsString(request);
// 			String redisKey = EVENT_CACHE_PREFIX + SessionDataKeyUtils.toKey(sessionDataKey) + ":" + eventType + ":"
// 				+ System.currentTimeMillis();
//
// 			redisTemplate.opsForValue().set(redisKey, eventDataJson);
// 			redisTemplate.opsForSet()
// 				.add(ACTIVE_SESSION_KEYS_SET, SessionDataKeyUtils.toKey(sessionDataKey)); // 활성 세션 등록 (선택 사항)
// 			log.info("Cached click event for session {}", sessionDataKey);
// 		} catch (Exception e) {
// 			log.error("Error caching click event: {}", e.getMessage(), e);
// 		}
// 	}
//
// 	/**
// 	 * 무브 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, Redis 에 캐싱합니다.
// 	 */
// 	@Override
// 	@Transactional
// 	public void createMoveEvent(PointerMoveEventRequest request) {
// 		log.info("이동 이벤트 로깅 및 Redis 캐싱: {}", request);
//
// 		SessionDataKey sessionDataKey = new SessionDataKey(
// 			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
// 		);
//
// 		sessionDataService.createOrFindSessionData(sessionDataKey); // SessionData 존재 확인 또는 생성
//
// 		try {
// 			String eventType = "movement";
// 			String eventDataJson = objectMapper.writeValueAsString(request);
// 			String redisKey = EVENT_CACHE_PREFIX + SessionDataKeyUtils.toKey(sessionDataKey) + ":" + eventType + ":"
// 				+ System.currentTimeMillis();
//
// 			redisTemplate.opsForValue().set(redisKey, eventDataJson);
// 			redisTemplate.opsForSet()
// 				.add(ACTIVE_SESSION_KEYS_SET, SessionDataKeyUtils.toKey(sessionDataKey)); // 활성 세션 등록 (선택 사항)
// 			log.info("Cached movement event for session {}", sessionDataKey);
// 		} catch (Exception e) {
// 			log.error("Error caching movement event: {}", e.getMessage(), e);
// 		}
// 	}
//
// 	/**
// 	 * 스크롤 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, Redis 에 캐싱합니다.
// 	 */
// 	@Override
// 	@Transactional
// 	public void createScrollEvent(PointerScrollEventRequest request) {
// 		log.info("스크롤 이벤트 로깅 및 Redis 캐싱: {}", request);
//
// 		SessionDataKey sessionDataKey = new SessionDataKey(
// 			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
// 		);
//
// 		sessionDataService.createOrFindSessionData(sessionDataKey); // SessionData 존재 확인 또는 생성
//
// 		try {
// 			String eventType = "scroll";
// 			String eventDataJson = objectMapper.writeValueAsString(request);
// 			String redisKey = EVENT_CACHE_PREFIX + SessionDataKeyUtils.toKey(sessionDataKey) + ":" + eventType + ":"
// 				+ System.currentTimeMillis();
//
// 			redisTemplate.opsForValue().set(redisKey, eventDataJson);
// 			redisTemplate.opsForSet()
// 				.add(ACTIVE_SESSION_KEYS_SET, SessionDataKeyUtils.toKey(sessionDataKey)); // 활성 세션 등록 (선택 사항)
// 			log.info("Cached scroll event for session {}", sessionDataKey);
// 		} catch (Exception e) {
// 			log.error("Error caching scroll event: {}", e.getMessage(), e);
// 		}
// 	}
//
// 	@Override
// 	@Transactional
// 	public void expireSession(String sessionId) {
// 		log.info("세션 종료 요청 처리 (Redis 기반)");
//
// 		SessionData data = sessionDataRepository.findBySessionId(sessionId)
// 			.orElseThrow();
//
// 		SessionDataKey sessionDataKey = new SessionDataKey(
// 			data.getSessionId(), data.getPageUrl(), data.getMemberSerialNumber()
// 		);
//
// 		// Redis에서 해당 세션 관련 캐시된 이벤트 삭제 (선택 사항)
// 		String eventKeyPattern = EVENT_CACHE_PREFIX + SessionDataKeyUtils.toKey(sessionDataKey) + "*";
// 		Set<String> keysToDelete = redisTemplate.keys(eventKeyPattern);
// 		if (keysToDelete != null && !keysToDelete.isEmpty()) {
// 			redisTemplate.delete(keysToDelete);
// 			log.info("Deleted {} cached events for session {}", keysToDelete.size(), sessionDataKey);
// 		}
//
// 		// Redis에서 활성 세션 목록에서 제거 (선택 사항)
// 		redisTemplate.opsForSet().remove(ACTIVE_SESSION_KEYS_SET, SessionDataKeyUtils.toKey(sessionDataKey));
//
// 		// 비활성 배치 처리는 스케줄러가 Redis에서 데이터를 읽어와 수행하므로,
// 		// 여기서는 Redis 정리 작업만 수행하거나, 필요하다면 스케줄러를 트리거하는 등의 로직 추가 가능
// 		log.info("Session {} marked for final processing by scheduler", sessionDataKey);
// 	}
// }
