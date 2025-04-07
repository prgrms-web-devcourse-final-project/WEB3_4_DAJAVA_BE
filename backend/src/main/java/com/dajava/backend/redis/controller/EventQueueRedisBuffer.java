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
	// buffer:{sessionKey} → 이벤트 큐
	private static final String BUFFER_PREFIX = "buffer:";
	// updated:{sessionKey} → 마지막 업데이트 시간 저장
	private static final String UPDATED_PREFIX = "updated:";

	// List 구조를 사용해서 이벤트 데이터를 순차적으로 저장
	public void addEvent(SessionDataKey sessionDataKey, T event) {
		String key = BUFFER_PREFIX + toKey(sessionDataKey);
		redisTemplate.opsForList().rightPush(key, event);

		String updatedKey = UPDATED_PREFIX + toKey(sessionDataKey);
		// 이벤트가 추가된 시점의 timestamp도 따로 저장
		redisTemplate.opsForValue().set(updatedKey, System.currentTimeMillis());
	}

	// 해당 key의 Redis 리스트를 전부 조회함
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