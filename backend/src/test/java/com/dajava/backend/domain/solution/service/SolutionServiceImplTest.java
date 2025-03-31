package com.dajava.backend.domain.solution.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.repository.SolutionDataRepository;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.solution.dto.SolutionInfoResponse;
import com.dajava.backend.domain.solution.entity.SolutionEntity;
import com.dajava.backend.domain.solution.exception.SolutionException;
import com.dajava.backend.domain.solution.repository.SolutionRepository;
import com.dajava.backend.global.exception.ErrorCode;
import com.dajava.backend.global.utils.PasswordUtils;

class SolutionServiceImplTest {

	@Mock
	private SolutionDataRepository solutionDataRepository;

	@Mock
	private RegisterRepository registerRepository;

	@Mock
	private SolutionRepository solutionRepository;

	@InjectMocks
	private SolutionServiceImpl solutionService;

	private SolutionData solutionData;
	private Register register;
	private SolutionEntity solutionEntity;


	private String serialNumber;
	private String correctPassword;
	private String inCorrectPassword;

	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);	//NPE 방지(mock객체 필드 초기화)

		serialNumber = "11db0706-4879-463a-a4d7-f7c347668cc6";
		correctPassword = "correctPassword";
		inCorrectPassword = "inCorrectPassword";
		solutionData = SolutionData.create(serialNumber);
		register = Register.builder()
			.serialNumber(serialNumber)
			.email("test@example.com")
			.password(PasswordUtils.hashPassword(correctPassword))
			.url("http://example.com")
			.startDate(LocalDateTime.now())
			.endDate(LocalDateTime.now().plusMonths(1))
			.duration(30)
			.isServiceExpired(false)
			.isSolutionComplete(false)
			.build();
		solutionEntity = new SolutionEntity();
		solutionEntity.setText("test");
		solutionEntity.setRegister(register);

	}
	@Test
	void getAISolution() {
	}

	@Test
	void getSolutionInfo_correctSerialNumberAndPassword() {
		//given
		when(registerRepository.findBySerialNumber(serialNumber)).thenReturn(register);
		when(solutionRepository.findByRegister(register)).thenReturn(Optional.of(solutionEntity));

		// when
		SolutionInfoResponse result = solutionService.getSolutionInfo(serialNumber, correctPassword);

		//then
		assertNotNull(result);
		assertEquals(result.text(), solutionEntity.getText());
		verify(registerRepository, times(1)).findBySerialNumber(serialNumber);
		verify(solutionRepository, times(1)).findByRegister(register);
	}

	@Test
	void getSolutionInfo_correctSerialNumberAndinCorrectPassword() {
		//given
		when(registerRepository.findBySerialNumber(serialNumber)).thenReturn(register);
		when(solutionRepository.findByRegister(register)).thenReturn(Optional.of(solutionEntity));

		// when
		SolutionException exception = assertThrows(SolutionException.class,
			() -> solutionService.getSolutionInfo(serialNumber, inCorrectPassword));

		//then
		assertEquals(ErrorCode.SOLUTION_PASSWORD_INVALID, exception.errorCode);
	}

	@Test
	void getSolutionData_success() {
		//given
		when(solutionDataRepository.findBySerialNumber(serialNumber)).thenReturn(solutionData);

		// when
		SolutionData result = solutionService.getSolutionData(serialNumber);

		// then
		assertNotNull(result);
		assertEquals(serialNumber, result.getSerialNumber());
		verify(solutionDataRepository, times(1)).findBySerialNumber(serialNumber);
	}

	@Test
	void getSolutionData_fail() {
		//given
		String wrongSerialNumber = "aaaa";
		when(solutionDataRepository.findBySerialNumber(wrongSerialNumber)).thenReturn(null);

		// when
		SolutionData result = solutionService.getSolutionData(wrongSerialNumber);

		// then
		assertNull(result);
		verify(solutionDataRepository, times(1)).findBySerialNumber(wrongSerialNumber);
	}
}