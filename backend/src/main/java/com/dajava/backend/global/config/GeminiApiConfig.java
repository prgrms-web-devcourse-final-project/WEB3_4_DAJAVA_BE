package com.dajava.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;

/**
 * Gemini API Key와 URL을 관리하고 WebClient 클래스를 따로 관리하기 위한 Configuration 클래스
 * @author sungkibum
 * @since 2025-04-01
 */
@Configuration
@Getter
public class GeminiApiConfig {

	@Value("${dajava.apiKey}")
	private String apiKey;

	@Value("${dajava.url}")
	private String apiUrl;

	@Bean
	public WebClient geminiWebClient() {
		return WebClient.builder()
			.baseUrl(getApiUrl())
			.defaultHeader("Content-Type", "application/json")
			.build();
	}
}
