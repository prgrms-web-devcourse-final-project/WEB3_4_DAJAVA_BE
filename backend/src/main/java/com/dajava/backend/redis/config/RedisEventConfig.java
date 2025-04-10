package com.dajava.backend.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;
import com.dajava.backend.global.utils.event.EventQueueRedisBuffer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RedisEventConfig {

	@Bean
	public EventQueueRedisBuffer<ClickEventRequest> clickBuffer(
		StringRedisTemplate redisTemplate,
		ObjectMapper objectMapper
	) {
		return new EventQueueRedisBuffer<>(redisTemplate, objectMapper, ClickEventRequest.class);
	}

	@Bean
	public EventQueueRedisBuffer<MovementEventRequest> moveBuffer(
		StringRedisTemplate redisTemplate,
		ObjectMapper objectMapper
	) {
		return new EventQueueRedisBuffer<>(redisTemplate, objectMapper, MovementEventRequest.class);
	}

	@Bean
	public EventQueueRedisBuffer<ScrollEventRequest> scrollBuffer(
		StringRedisTemplate redisTemplate,
		ObjectMapper objectMapper
	) {
		return new EventQueueRedisBuffer<>(redisTemplate, objectMapper, ScrollEventRequest.class);
	}
}
