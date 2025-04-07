package com.dajava.backend.domain.register.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterCreateResponse;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.global.utils.PasswordUtils;
import com.dajava.backend.global.utils.TimeUtils;

@ActiveProfiles("test")
@SpringBootTest
public class RegisterCacheServiceTest {

	@Autowired
	private RegisterRepository registerRepository;

	@Autowired
	private RegisterCacheService registerCacheService;

	@Autowired
	private RegisterService registerService;

	@BeforeEach
	void setUp() {
		registerRepository.deleteAll();
	}

	@Test
	@DisplayName("1. 진행중인 서비스가 Cache 에 담기는지 테스트")
	void t001() {
		// Given
		LocalDateTime start = LocalDateTime.now().minusDays(1).withMinute(0).withSecond(0).withNano(0);

		RegisterCreateRequest request = new RegisterCreateRequest(
			"testEmail@gmail.com",
			"testPassword1!",
			"localhost:3000/testUrl",
			start,
			start.plusDays(7)
		);

		Register register = Register.builder()
			.serialNumber("cache_test_serial")
			.email(request.email())
			.password(PasswordUtils.hashPassword(request.password()))
			.url(request.url())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.duration(TimeUtils.getDuration(request.startDate(), request.endDate()))
			.isServiceExpired(false)
			.isSolutionComplete(false)
			.captureData(new ArrayList<>())
			.build();

		registerRepository.save(register);

		String progressSerialNumber = register.getSerialNumber();

		// When
		registerCacheService.refreshCache();

		// Then
		assertEquals(2, registerCacheService.getSerialNumberCache().size());
		assertTrue(registerCacheService.isValidSerialNumber(progressSerialNumber));
	}

	@Test
	@DisplayName("2. 진행중이지 않은 테스트는 Cache 에 담기지 않음")
	void t002() {
		// Given
		LocalDateTime nextDay = LocalDateTime.now().plusDays(1).withMinute(0).withSecond(0).withNano(0);
		RegisterCreateRequest request = new RegisterCreateRequest(
			"testEmail2@gmail.com",
			"testPassword1!",
			"localhost:3000/testUrl2",
			nextDay,
			nextDay.plusDays(7)
		);

		RegisterCreateResponse response = registerService.createRegister(request);
		String notProgressSerialNumber = response.serialNumber();

		// When
		registerCacheService.refreshCache();

		// Then
		assertFalse(registerCacheService.isValidSerialNumber(notProgressSerialNumber));
	}
}
