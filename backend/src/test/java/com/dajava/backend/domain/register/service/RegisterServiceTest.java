package com.dajava.backend.domain.register.service;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.dajava.backend.domain.register.dto.SolutionCreateRequest;
import com.dajava.backend.domain.register.dto.SolutionCreateResponse;
import com.dajava.backend.domain.register.repository.SolutionRepository;

@ActiveProfiles("test")
@SpringBootTest
class RegisterServiceTest {

	@Autowired
	RegisterService service;

	@Autowired
	SolutionRepository repository;

	@Test
	@DisplayName("솔루션 생성")
	public void t1() {
		SolutionCreateRequest request = new SolutionCreateRequest(
			"chsan626@gmail.com",
			"password123!",
			"localhost:3000/test123",
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(7)
		);

		SolutionCreateResponse response = service.createSolution(request);
		Assertions.assertNotNull(response);
		Assertions.assertEquals(1, repository.findAll().size());
	}
}
