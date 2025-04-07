package com.dajava.backend.redis.controller;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.global.utils.SessionDataKeyUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventQueueRedisBuffer<T> {
	private static final String EVENT_CACHE_PREFIX = "event:";
	private static final String LAST_UPDATED_PREFIX = "lastUpdated:";
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Class<T> clazz;

	public void addEvent(SessionDataKey sessionDataKey, T event) {
		String key = buildKey(sessionDataKey);
		try {
			String json = objectMapper.writeValueAsString(event);
			redisTemplate.opsForList().leftPush(key, json);
			redisTemplate.expire(key, 1, TimeUnit.HOURS); // 만료 시간 설정

			// 마지막 업데이트 시간 기록
			redisTemplate.opsForValue().set(LAST_UPDATED_PREFIX + key, String.valueOf(System.currentTimeMillis()));
		} catch (Exception e) {
			throw new RuntimeException("Redis에 이벤트 저장 실패", e);
		}
	}

	public List<T> getEvents(SessionDataKey sessionDataKey) {
		String key = buildKey(sessionDataKey);
		List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);

		if (jsonList == null) return Collections.emptyList();

		return jsonList.stream()
			.map(json -> {
				try {
					return objectMapper.readValue(json, clazz);
				} catch (Exception e) {
					return null;
				}
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public List<T> flushEvents(SessionDataKey sessionDataKey) {
		List<T> events = getEvents(sessionDataKey);
		String key = buildKey(sessionDataKey);
		redisTemplate.delete(key);
		redisTemplate.delete(LAST_UPDATED_PREFIX + key);
		return events;
	}

	public Set<SessionDataKey> getActiveSessionKeys() {
		Set<String> keys = redisTemplate.keys(EVENT_CACHE_PREFIX + "*");
		if (keys == null) return Collections.emptySet();
		return keys.stream()
			.map(k -> k.replace(EVENT_CACHE_PREFIX, ""))
			.map(SessionDataKeyUtils::parseKey)
			.collect(Collectors.toSet());
	}

	public Long getLastUpdated(String key) {
		String value = redisTemplate.opsForValue().get(LAST_UPDATED_PREFIX + key);
		return value != null ? Long.valueOf(value) : null;
	}

	public void clearAll() {
		Set<String> keys = redisTemplate.keys(EVENT_CACHE_PREFIX + "*");
		Set<String> updatedKeys = redisTemplate.keys(LAST_UPDATED_PREFIX + "*");

		if (keys != null) redisTemplate.delete(keys);
		if (updatedKeys != null) redisTemplate.delete(updatedKeys);
	}

	private String buildKey(SessionDataKey key) {
		return EVENT_CACHE_PREFIX + SessionDataKeyUtils.toKey(key);
	}
}