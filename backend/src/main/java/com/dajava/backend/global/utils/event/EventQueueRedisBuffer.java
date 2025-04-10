package com.dajava.backend.global.utils.event;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;
import com.dajava.backend.domain.log.exception.LogException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.dajava.backend.global.exception.ErrorCode.*;

public class EventQueueRedisBuffer<T> {

	private final StringRedisTemplate redisTemplate;
	private final EventSerializer<T> serializer;
	private final MetadataManager metadataManager;

	public EventQueueRedisBuffer(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, Class<T> clazz) {
		this.redisTemplate = redisTemplate;
		this.serializer = new EventSerializer<>(objectMapper, clazz);
		this.metadataManager = new MetadataManager(redisTemplate);
	}

	// Todo...
	public void cacheEvents(SessionIdentifier sessionIdentifier, T event) {
		String eventKey = KeyGenerator.buildEventKey(sessionIdentifier);
		String updatedKey = KeyGenerator.buildLastUpdatedKey(eventKey);
		try {
			String json = serializer.serialize(event);
			redisTemplate.opsForList().leftPush(eventKey, json);
			redisTemplate.expire(eventKey, 1, TimeUnit.HOURS);
			metadataManager.updateLastUpdated(updatedKey);
		} catch (Exception e) {
			throw new LogException(REDIS_CACHING_ERROR);
		}
	}

	public List<T> getEvents(SessionIdentifier sessionIdentifier) {
		String key = KeyGenerator.buildEventKey(sessionIdentifier);
		List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);

		if (jsonList == null || jsonList.isEmpty()) return Collections.emptyList();

		return jsonList.stream()
			.map(serializer::deserialize)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	public List<T> flushEvents(SessionIdentifier sessionIdentifier) {
		String eventKey = KeyGenerator.buildEventKey(sessionIdentifier);
		String updatedKey = KeyGenerator.buildLastUpdatedKey(eventKey);

		List<T> events = getEvents(sessionIdentifier);
		redisTemplate.delete(eventKey);
		redisTemplate.delete(updatedKey);
		return events;
	}

	public Long getLastUpdated(String key) {
		String updatedKey = KeyGenerator.buildLastUpdatedKey(key);
		return metadataManager.getLastUpdated(updatedKey);
	}

	public void clearAll() {
		metadataManager.clearKeysByPattern("event:*");
		metadataManager.clearKeysByPattern("lastUpdated:*");
	}
}
