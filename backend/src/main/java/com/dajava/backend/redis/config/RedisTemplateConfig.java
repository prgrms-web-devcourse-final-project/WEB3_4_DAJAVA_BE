package com.dajava.backend.redis.config;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisTemplateConfig {
	// 일반적인 Object용 템플릿
	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	// SessionDataDocument 전용 템플릿
	// @Bean(name = "sessionDataRedisTemplate")
	// public RedisTemplate<String, SessionDataDocument> sessionDataRedisTemplate(RedisConnectionFactory connectionFactory) {
	// 	ObjectMapper objectMapper = new ObjectMapper();
	// 	objectMapper.registerModule(new JavaTimeModule());
	// 	objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	//
	// 	GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
	// 	RedisTemplate<String, SessionDataDocument> template = new RedisTemplate<>();
	// 	template.setConnectionFactory(connectionFactory);
	// 	template.setKeySerializer(new StringRedisSerializer());
	// 	template.setValueSerializer(serializer);
	// 	return template;
	// }
}
