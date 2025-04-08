package com.dajava.backend.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@Profile("!prod")
@OpenAPIDefinition(info = @Info(title = "사용자 패턴 분석 API 서버", version = "v1"))
public class DevSpringdocConfig {
	public static final String[] SWAGGER_PATHS = {
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/swagger-resources/**",
		"/swagger-ui.html"
	};

	@Bean
	public GroupedOpenApi groupApiV1() {
		return GroupedOpenApi.builder()
			.group("apiV1")
			.pathsToMatch("/v1/**")
			.build();
	}
}
