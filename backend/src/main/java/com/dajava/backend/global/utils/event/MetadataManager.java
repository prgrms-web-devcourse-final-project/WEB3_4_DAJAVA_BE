package com.dajava.backend.global.utils.event;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

public class MetadataManager {
	private final StringRedisTemplate redisTemplate;

	public MetadataManager(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void updateLastUpdated(String lastUpdatedKey) {
		redisTemplate.opsForValue().set(lastUpdatedKey, String.valueOf(System.currentTimeMillis()));
	}

	public Long getLastUpdated(String lastUpdatedKey) {
		String value = redisTemplate.opsForValue().get(lastUpdatedKey);
		return value != null ? Long.valueOf(value) : null;
	}

	public void clearKeysByPattern(String pattern) {
		Set<String> keys = redisTemplate.keys(pattern);
		if (keys != null) redisTemplate.delete(keys);
	}
}
