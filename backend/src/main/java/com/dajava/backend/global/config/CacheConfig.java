package com.dajava.backend.global.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager cacheManager = new CaffeineCacheManager("heatmapCache");
		cacheManager.setCaffeine(caffeineCacheBuilder());
		return cacheManager;
	}

	private Caffeine<Object, Object> caffeineCacheBuilder() {
		return Caffeine.newBuilder()
			.initialCapacity(100)
			.maximumSize(500)
			.expireAfterWrite(5, TimeUnit.MINUTES)
			.recordStats();
	}
}
