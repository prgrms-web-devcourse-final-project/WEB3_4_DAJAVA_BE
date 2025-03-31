package com.dajava.backend.domain.solution.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;
import com.dajava.backend.domain.solution.dto.SolutionRequestDto;
import com.dajava.backend.domain.solution.dto.SolutionRequestDto.EventDataDto;
import com.dajava.backend.domain.solution.exception.SolutionException;
import com.dajava.backend.global.utils.SolutionUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;

class SolutionTest {

	private SolutionData mockSolutionData;
	private List<SolutionEvent> mockSolutionEvents;

	@BeforeEach
	void setUp() {
		mockSolutionData = mock(SolutionData.class);
		mockSolutionEvents = List.of(mock(SolutionEvent.class), mock(SolutionEvent.class));

		when(mockSolutionData.getSerialNumber()).thenReturn("12345");
		when(mockSolutionData.getSolutionEvents()).thenReturn(mockSolutionEvents);
	}

	// @Test
	// @DisplayName("refinePrompt: 세션 데이터로 프롬프트 생성")
	// void testRefinePrompt() {
	// 	SolutionRequestDto dto = new SolutionRequestDto(
	// 		"12345",
	// 		Arrays.asList(new EventDataDto("session1", 1712000000L, "click", 100, 200, "button", "http://example.com", 1920))
	// 	);
	//
	// 	String result = SolutionUtils.refinePrompt(dto);
	// 	assertTrue(result.contains("UI/UX 개선점을 제안해주세요"));
	// 	assertTrue(result.contains("12345"));
	// }

	@Test
	@DisplayName("buildRefineData: 프롬프트를 JSON 형식으로 변환")
	void testBuildRefineData() {
		String prompt = "UI/UX 개선을 제안해주세요.";
		String result = SolutionUtils.buildRefineData(prompt);

		assertTrue(result.contains("\"text\": \"UI/UX 개선을 제안해주세요.\""));
		assertTrue(result.startsWith("{\"contents\": [{\"parts\": [{\"text\": "));
	}

	@Test
	@DisplayName("extractSerialNumber: requestDto 문자열에서 serialNumber 추출")
	void testExtractSerialNumber() {
		String dtoString = "SolutionRequestDto[serialNumber=12345, eventData=[]]";
		String result = SolutionUtils.extractSerialNumber(dtoString);

		assertEquals("12345", result);
	}

	@Test
	@DisplayName("extractSerialNumber: SolutionData에서 serialNumber 추출")
	void testExtractsSerialNumber() {
		String result = SolutionUtils.extractsSerialNumber(mockSolutionData);
		assertEquals("12345", result);
	}

	@Test
	@DisplayName("extractSerialNumber: SolutionData가 null이면 예외 발생")
	void testExtractsSerialNumber_NullData() {
		assertThrows(SolutionException.class, () -> SolutionUtils.extractsSerialNumber(null));
	}

	@Test
	@DisplayName("extractSolutionEvents: SolutionData에서 이벤트 목록 추출")
	void testExtractSolutionEvents() {
		List<SolutionEvent> result = SolutionUtils.extractSolutionEvents(mockSolutionData);
		assertEquals(mockSolutionEvents, result);
	}

	@Test
	@DisplayName("extractSolutionEvents: SolutionData가 null이면 예외 발생")
	void testExtractSolutionEvents_NullData() {
		assertThrows(SolutionException.class, () -> SolutionUtils.extractSolutionEvents(null));
	}
}