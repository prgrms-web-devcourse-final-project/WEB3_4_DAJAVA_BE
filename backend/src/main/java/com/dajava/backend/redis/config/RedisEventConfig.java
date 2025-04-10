package com.dajava.backend.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.global.utils.EventQueueRedisBuffer;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Redis 키를 다르게 구성하기 위해 나눔
 */
@Configuration
public class RedisEventConfig {
	@Bean
	public EventQueueRedisBuffer<ClickEventRequest> clickBuffer(
		StringRedisTemplate redisTemplate,
		ObjectMapper objectMapper
	) {
		return new EventQueueRedisBuffer<>(redisTemplate, objectMapper, ClickEventRequest.class);
	}
}