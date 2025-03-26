package com.dajava.backend.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(AiConfig.class)
public class WebClientConfig {
	private final AiConfig aiConfig;
	public WebClientConfig(AiConfig aiConfig) {
		this.aiConfig = aiConfig;
	}
	@Bean
	public WebClient webClient() {
		return WebClient.builder()
			.baseUrl(aiConfig.getApiUrl())
			.defaultHeader("Content-Type", "application/json")
			.build();
	}
}
