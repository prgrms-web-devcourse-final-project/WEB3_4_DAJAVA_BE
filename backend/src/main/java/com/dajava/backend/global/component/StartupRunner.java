package com.dajava.backend.global.component;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.service.RegisterService;

@Component
public class StartupRunner implements CommandLineRunner {

	private final RegisterService registerService;

	public StartupRunner(RegisterService registerService) {
		this.registerService = registerService;
	}

	@Override
	public void run(String... args) throws Exception {
		// 테스트용 Register 생성 요청 DTO 생성
		RegisterCreateRequest testRequest = new RegisterCreateRequest(
			"user@example.com",
			"password123!",
			"localhost:3000/myTestPage",
			LocalDateTime.now().minusDays(2).withHour(0).withMinute(0).withSecond(0).withNano(0),
			LocalDateTime.now().plusDays(5).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		// createTestRegister 메서드를 호출하여 테스트 Register 1회 생성
		registerService.createTestRegister(testRequest);
	}
}
