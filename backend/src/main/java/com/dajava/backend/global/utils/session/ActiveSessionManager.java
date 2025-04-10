package com.dajava.backend.global.utils.session;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.global.utils.EventQueueRedisBuffer;
import com.dajava.backend.global.utils.SessionDataKeyUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActiveSessionManager {
	private static final String EVENT_CACHE_PREFIX = "event:";
	private final StringRedisTemplate redisTemplate;

	// 특정 이벤트 타입에 대한 세션 키 조회
	public Set<SessionDataKey> getActiveSessionKeysForType(String eventTypePrefix) {
		Set<String> keys = redisTemplate.keys(EVENT_CACHE_PREFIX + eventTypePrefix + "*");
		if (keys == null) return Collections.emptySet();

		return keys.stream()
			.map(k -> k.replace(EVENT_CACHE_PREFIX, ""))
			.map(SessionDataKeyUtils::parseKey)
			.collect(Collectors.toSet());
	}
}
