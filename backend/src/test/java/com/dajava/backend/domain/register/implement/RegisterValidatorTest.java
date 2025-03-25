package com.dajava.backend.domain.register.implement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import com.dajava.backend.domain.register.dto.RegisterCreateRequest;
import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.domain.register.repository.SolutionRepository;

@ActiveProfiles("test")
class RegisterValidatorTest {

	@InjectMocks
	private RegisterValidator validator;

	@Mock
	private SolutionRepository repository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("SolutionCreateRequest-실패 : Email 형식이 잘못 됨")
	void t1() {
		RegisterCreateRequest request = new RegisterCreateRequest(
			"invalid-email", // 잘못된 형식의 이메일
			"password123",
			"localhost:3000/test",
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(7)
		);

		// when & then
		assertThrows(RegisterException.class, () -> {
			validator.validateCreateRequest(request);
		});
	}

	@Test
	@DisplayName("SolutionCreateRequest-실패 : 이미 등록됨")
	void t2() {
		// given
		String validUrl = "localhost:3000/test";
		RegisterCreateRequest request = new RegisterCreateRequest(
			"test@example.com",
			"password123",
			validUrl,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(7)
		);

		// URL 유효성 검사에서 false 반환하도록 설정 (이미 등록됨)
		when(repository.checkUrlAvailability(eq(validUrl), any(LocalDateTime.class)))
			.thenReturn(false);

		// when & then
		assertThrows(RegisterException.class, () -> {
			validator.validateCreateRequest(request);
		});
	}
}
