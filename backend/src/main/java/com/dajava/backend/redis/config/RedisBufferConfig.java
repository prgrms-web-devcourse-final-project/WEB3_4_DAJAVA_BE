package com.dajava.backend.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.redis.controller.EventQueueRedisBuffer;

@Configuration
public class RedisBufferConfig {

	@Bean
	public EventQueueRedisBuffer<PointerClickEventRequest> clickBuffer(StringRedisTemplate redisTemplate) {
		return new EventQueueRedisBuffer<>(redisTemplate, PointerClickEventRequest.class);
	}

	@Bean
	public EventQueueRedisBuffer<PointerMoveEventRequest> moveBuffer(StringRedisTemplate redisTemplate) {
		return new EventQueueRedisBuffer<>(redisTemplate, PointerMoveEventRequest.class);
	}

	@Bean
	public EventQueueRedisBuffer<PointerScrollEventRequest> scrollBuffer(StringRedisTemplate redisTemplate) {
		return new EventQueueRedisBuffer<>(redisTemplate, PointerScrollEventRequest.class);
	}
}