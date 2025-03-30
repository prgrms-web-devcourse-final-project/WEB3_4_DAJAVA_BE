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

import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterModifyRequest;
import com.dajava.backend.domain.register.dto.register.RegistersInfoRequest;
import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.domain.register.repository.RegisterRepository;

@ActiveProfiles("test")
class RegisterValidatorTest {

	@InjectMocks
	private RegisterValidator validator;

	@Mock
	private RegisterRepository repository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("RegisterCreateRequest-실패 : Email 형식이 잘못 됨")
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
	@DisplayName("RegisterCreateRequest-실패 : 이미 등록됨")
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

	@Test
	@DisplayName("RegisterModifyRequest-실패 : 존재하지 않는 솔루션 요청")
	void t3() {
		RegisterModifyRequest request = new RegisterModifyRequest(
			LocalDateTime.now().plusDays(7)
		);
		Long solutionId = 1000L;

		assertThrows(RegisterException.class, () -> {
			validator.validateModifyRequest(request, solutionId);
		});
	}

	@Test
	@DisplayName("RegisterDeleteRequest-실패: 존재하지 않는 솔루션을 요청")
	void t4() {
		Long solutionId = 1000L;

		assertThrows(RegisterException.class, () -> {
			validator.validateDeleteRequest(solutionId);
		});
	}

	@Test
	@DisplayName("RegistersInfoRequest-실패: 올바르지 않은 페이지 요청")
	void t5() {
		RegistersInfoRequest request = new RegistersInfoRequest(0, 2);
		assertThrows(RegisterException.class, () -> {
			validator.validateInfoRequest(request);
		});
	}
}
