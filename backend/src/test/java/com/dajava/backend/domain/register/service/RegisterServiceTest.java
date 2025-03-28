package com.dajava.backend.domain.register.service;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.dajava.backend.domain.register.dto.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.RegisterCreateResponse;
import com.dajava.backend.domain.register.repository.RegisterRepository;

@ActiveProfiles("test")
@SpringBootTest
class RegisterServiceTest {

	@Autowired
	RegisterService service;

	@Autowired
	RegisterRepository repository;

	@Test
	@DisplayName("솔루션 생성")
	public void t1() {
		RegisterCreateRequest request = new RegisterCreateRequest(
			"chsan626@gmail.com",
			"password123!",
			"localhost:3000/test123",
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(7)
		);

		RegisterCreateResponse response = service.createRegister(request);
		Assertions.assertNotNull(response);
		Assertions.assertEquals(1, repository.findAll().size());
	}
}
