package com.dajava.backend.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.dajava.backend.domain.log.dto.ClickEventRequest;
import com.dajava.backend.domain.log.dto.MovementEventRequest;
import com.dajava.backend.domain.log.dto.ScrollEventRequest;
import com.dajava.backend.global.utils.event.EventQueueRedisBuffer;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *  클라이언트에서 전달한 json 형태 데이터를 string 타입으로 역질렬화 한후 redis에 저장
 * 	각각 해당하는 이벤트 dto를 통해 저장
 */
@Configuration
public class RedisEventConfig {
	/**
	 * click 이벤트로 저장
	 */
	@Bean
	public EventQueueRedisBuffer<ClickEventRequest> clickBuffer(
		StringRedisTemplate redisTemplate,
		ObjectMapper objectMapper
	) {
		return new EventQueueRedisBuffer<>(redisTemplate, objectMapper, ClickEventRequest.class);
	}
	/**
	 * move 이벤트로 저장
	 */
	@Bean
	public EventQueueRedisBuffer<MovementEventRequest> moveBuffer(
		StringRedisTemplate redisTemplate,
		ObjectMapper objectMapper
	) {
		return new EventQueueRedisBuffer<>(redisTemplate, objectMapper, MovementEventRequest.class);
	}
	/**
	 * scroll 이벤트로 저장
	 */
	@Bean
	public EventQueueRedisBuffer<ScrollEventRequest> scrollBuffer(
		StringRedisTemplate redisTemplate,
		ObjectMapper objectMapper
	) {
		return new EventQueueRedisBuffer<>(redisTemplate, objectMapper, ScrollEventRequest.class);
	}
}
