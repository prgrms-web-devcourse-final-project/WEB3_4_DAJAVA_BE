package com.dajava.backend.domain.solution.utill;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.global.utils.SolutionUtils;
import com.dajava.backend.domain.solution.dto.SolutionRequestDto;

/**
 * SolutionUtils의 메서드를 테스트하는 클래스입니다.
 * 이 클래스는 SolutionRequestDto와 관련된 유틸리티 메서드를 검증합니다.
 */
class SolutionUtilTest {

	/** 테스트에 사용될 mock SolutionRequestDto 객체 */
	private SolutionRequestDto mockSolutionRequestDto;

	/**
	 * 테스트 실행 전, 독립성을 위해 초기화
	 * 이 메서드는 각 테스트 실행 전에 호출되어 mockSolutionRequestDto 객체를 준비합니다.
	 */
	@BeforeEach
	@DisplayName("테스트 실행 전, mockSolutionEvent mockSolutionData 초기화")
	void setUp() {
		SolutionRequestDto.EventDataDto dummy = new SolutionRequestDto.EventDataDto(
			"session1", // 세션 ID
			LocalDateTime.now().minusMinutes(10), // 이벤트 발생 시간
			"click", // 이벤트 타입
			100, // 클릭 X 좌표
			200, // 클릭 Y 좌표
			"button", // 이벤트가 발생한 UI 요소
			"http://example.com", // 페이지 URL
			1920 // 브라우저 width
		);
		mockSolutionRequestDto = new SolutionRequestDto("12345", List.of(dummy));
	}

	/**
	 * SolutionRequestDto에서 serialNumber 값을 추출하는 테스트입니다.
	 * @see SolutionUtils#extractsSerialNumber(SolutionRequestDto)
	 */
	@Test
	@DisplayName("SolutionRequestDto에서 serialNumber 추출")
	void testExtractsSerialNumber() {
		// SolutionRequestDto에서 serialNumber 추출
		String result = SolutionUtils.extractsSerialNumber(mockSolutionRequestDto);

		// serialNumber가 올바르게 추출되는지 확인
		assertEquals("12345", result);
	}

	/**
	 * SolutionRequestDto에서 event data를 추출하는 테스트입니다.
	 * @see SolutionUtils#extractSolutionEvents(SolutionRequestDto)
	 */
	@Test
	@DisplayName("SolutionRequestDto에서 event data 추출")
	void testExtractsEventData() {
		// SolutionRequestDto에서 eventData 추출
		List<SolutionRequestDto.EventDataDto> result = SolutionUtils.extractSolutionEvents(mockSolutionRequestDto);

		// eventData가 올바르게 추출되는지 확인
		assertEquals(mockSolutionRequestDto.eventData(), result);
	}

	/**
	 * 주어진 세션 데이터로부터 프롬프트 문자열이 올바르게 생성되는지 테스트합니다.
	 * @see SolutionUtils#refinePrompt(List)
	 */
	@Test
	@DisplayName("String 형태 변화")
	void testRefinePrompt() {
		String expectedPrompt = "다음 사용자 세션 데이터를 분석하여 UI/UX 개선점을 제안해주세요. 브라우저 width는 1024px이고 브라우저 환경은 Chrome이야:";

		// eventData 추출
		List<SolutionRequestDto.EventDataDto> eventData = mockSolutionRequestDto.eventData();

		// SolutionUtils.refinePrompt 메서드 실행
		String result = SolutionUtils.refinePrompt(eventData);

		// 결과가 null이 아님을 확인
		assertNotNull(result, "결과가 null이 아닙니다.");

		// 결과가 예상된 프롬프트를 포함하는지 확인
		assertTrue(result.contains(expectedPrompt), "결과가 예상된 프롬프트를 포함해야 합니다.");
	}

	/**
	 * 주어진 프롬프트를 JSON 형식으로 변환하는 메서드의 동작을 테스트합니다.
	 * @see SolutionUtils#buildRefineData(String)
	 */
	@Test
	@DisplayName("AI 솔루션 프롬프트 생성")
	void testBuildRefineData() {
		String prompt = "다음 사용자 세션 데이터를 분석하여 UI/UX 개선점을 제안해주세요. 브라우저 width는 1024px이고 브라우저 환경은 Chrome이야: 테스트 프롬프트";

		// SolutionUtils.buildRefineData 메서드 실행
		String result = SolutionUtils.buildRefineData(prompt);

		// 결과가 null이 아님을 확인
		assertNotNull(result);

		// 결과가 입력된 프롬프트를 포함하는지 확인
		assertTrue(result.contains(prompt));
	}

}
