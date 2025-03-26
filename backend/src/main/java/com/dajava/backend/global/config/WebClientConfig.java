package com.dajava.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	@Value("${DAJAVA_AI_API_KEY}")
	private String API_KEY;
	@Value("${DAJAVA_AI_API_URL}")
	private String API_URL;

	@Bean
	public WebClient geminiWebClient() {
		return WebClient.builder()
			.baseUrl(API_URL)
			.defaultHeader("Content-Type", "application/json")
			.defaultHeader("Authorization", "Bearer " + API_KEY)
			.build();
	}
}

