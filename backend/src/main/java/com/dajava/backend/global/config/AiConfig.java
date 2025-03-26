package com.dajava.backend.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "dajava.ai")
@Getter
@Setter
public class AiConfig {
	private String apiKey;
	private String apiUrl;
}
