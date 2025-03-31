package com.dajava.backend.domain.solution.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.repository.SolutionDataRepository;

class SolutionServiceImplTest {

	@Mock
	private SolutionDataRepository solutionDataRepository;

	@InjectMocks
	private SolutionServiceImpl solutionService;

	private SolutionData solutionData;
	private String serialNumber;

	@BeforeEach
	void beforeEach() {
		MockitoAnnotations.openMocks(this);	//NPE 방(mock객체 필드 초기화)
		serialNumber = "11db0706-4879-463a-a4d7-f7c347668cc6";
		solutionData = SolutionData.create(serialNumber);
	}
	@Test
	void getAISolution() {
	}

	@Test
	void getSolutionInfo() {
	}

	@Test
	void getSolutionData_success() {

		when(solutionDataRepository.findBySerialNumber(serialNumber)).thenReturn(solutionData);

		// when
		SolutionData result = solutionService.getSolutionData(serialNumber);

		// then
		assertNotNull(result);
		assertEquals(serialNumber, result.getSerialNumber());
		verify(solutionDataRepository, times(1)).findBySerialNumber(serialNumber);
	}
}