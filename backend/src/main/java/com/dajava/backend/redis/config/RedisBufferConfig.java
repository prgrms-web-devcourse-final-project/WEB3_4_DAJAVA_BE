package com.dajava.backend.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.redis.controller.EventQueueRedisBuffer;

@Configuration
public class RedisBufferConfig {

	@Bean
	public EventQueueRedisBuffer<PointerClickEventRequest> clickBuffer(RedisTemplate<String, Object> redisTemplate) {
		return new EventQueueRedisBuffer<>(redisTemplate);
	}

	@Bean
	public EventQueueRedisBuffer<PointerMoveEventRequest> moveBuffer(RedisTemplate<String, Object> redisTemplate) {
		return new EventQueueRedisBuffer<>(redisTemplate);
	}

	@Bean
	public EventQueueRedisBuffer<PointerScrollEventRequest> scrollBuffer(RedisTemplate<String, Object> redisTemplate) {
		return new EventQueueRedisBuffer<>(redisTemplate);
	}
}