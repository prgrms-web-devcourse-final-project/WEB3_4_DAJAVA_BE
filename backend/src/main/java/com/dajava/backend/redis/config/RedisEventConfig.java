package com.dajava.backend.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.redis.utils.EventQueueRedisBuffer;

/**
 * Redis 키를 다르게 구성하기 위해 나눔
 */
@Configuration
public class RedisEventConfig {

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