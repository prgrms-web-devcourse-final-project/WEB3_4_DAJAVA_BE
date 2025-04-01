package com.dajava.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class GeminiApiConfig {

	@Value("${dajava.apiKey}")
	private String apiKey;

	@Value("${dajava.url}")
	private String apiUrl;
}
