package com.dajava.backend.global.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dajava.backend.domain.register.service.RegisterCacheService;
import com.dajava.backend.global.filter.LogRequestFilter;

/**
 * 엔드포인트 클릭, 이동, 스크롤 요청에 대해 필터 검사를 하기 위한 설정입니다.
 * @author Metronon
 * @since 2025-03-27
 */
@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<LogRequestFilter> logRequestFilter(RegisterCacheService registerCacheService) {
		FilterRegistrationBean<LogRequestFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new LogRequestFilter(registerCacheService));
		registrationBean.addUrlPatterns("/log/click", "/log/movement", "/log/scroll");
		registrationBean.setOrder(1);
		return registrationBean;
	}
}
