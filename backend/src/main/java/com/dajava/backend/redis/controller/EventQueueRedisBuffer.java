package com.dajava.backend.redis.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.global.utils.SessionDataKeyUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventQueueRedisBuffer<T> {
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String BUFFER_PREFIX = "buffer:";
	private static final String UPDATED_PREFIX = "updated:";

	public void addEvent(SessionDataKey sessionDataKey, T event) {
		String key = BUFFER_PREFIX + toKey(sessionDataKey);
		redisTemplate.opsForList().rightPush(key, event);

		String updatedKey = UPDATED_PREFIX + toKey(sessionDataKey);
		redisTemplate.opsForValue().set(updatedKey, System.currentTimeMillis());
	}

	public List<T> getEvents(SessionDataKey sessionDataKey) {
		String key = BUFFER_PREFIX + toKey(sessionDataKey);
		List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
		return objects.stream().map(obj -> (T) obj).collect(Collectors.toList());
	}

	public List<T> flushEvents(SessionDataKey sessionDataKey) {
		String key = BUFFER_PREFIX + toKey(sessionDataKey);
		List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
		redisTemplate.delete(key);
		redisTemplate.delete(UPDATED_PREFIX + toKey(sessionDataKey));
		return objects.stream().map(obj -> (T) obj).collect(Collectors.toList());
	}

	public void clearAll() {
		Set<String> keys = redisTemplate.keys(BUFFER_PREFIX + "*");
		redisTemplate.delete(keys);
		Set<String> updatedKeys = redisTemplate.keys(UPDATED_PREFIX + "*");
		redisTemplate.delete(updatedKeys);
	}

	public Set<SessionDataKey> getActiveSessionKeys() {
		Set<String> keys = redisTemplate.keys(BUFFER_PREFIX + "*");
		return keys.stream()
			.map(k -> k.replace(BUFFER_PREFIX, ""))
			.map(SessionDataKeyUtils::parseKey)
			.collect(Collectors.toSet());
	}

	private String toKey(SessionDataKey sessionDataKey) {
		return SessionDataKeyUtils.toKey(sessionDataKey);
	}
}