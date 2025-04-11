package com.dajava.backend.global.utils.session;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.dajava.backend.global.utils.LogUtils;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActiveSessionManager {
	private static final String EVENT_CACHE_PREFIX = "event:";
	private final StringRedisTemplate redisTemplate;

	// 특정 이벤트 타입에 대한 세션 키 조회
	public Set<SessionIdentifier> getActiveSessionKeysForType(String eventTypeSuffix) {
		// 예: event:*:scroll 형식으로 Redis 키 조회
		Set<String> keys = redisTemplate.keys(EVENT_CACHE_PREFIX + "*:" + eventTypeSuffix);
		if (keys == null) return Collections.emptySet();

		return keys.stream()
			.map(k -> {
				// event:제외 → test-session-id|localhost:3000|5_team_testSerial:scroll
				String body = k.substring(EVENT_CACHE_PREFIX.length());
				// 마지막 콜론 기준으로 잘라서 이벤트 타입 제외 → test-session-id|localhost:3000|5_team_testSerial
				int lastColonIndex = body.lastIndexOf(':');
				if (lastColonIndex == -1) return null;
				String sessionPart = body.substring(0, lastColonIndex);
				return LogUtils.parseRedisKey(sessionPart); // SessionIdentifier로 변환
			})
			.filter(k -> k != null)
			.collect(Collectors.toSet());
	}
}