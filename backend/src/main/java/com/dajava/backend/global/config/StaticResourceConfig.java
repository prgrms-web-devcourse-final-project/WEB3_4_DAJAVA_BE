package com.dajava.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 로컬로 저장된 이미지의 경로 매핑을 위한 Config
 * @author Metronon
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/page-capture/**")
			.addResourceLocations("file:///C:/page-capture/");
	}
}
