package com.dajava.backend.domain.solution.dto;

import java.util.List;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;

/**
 * SolutionData 객체 SolutionRequestDto 변환 여부 테스트
 * 이 테스트는 SolutionRequestDto의 필드들이 올바르게 변환되었는지 확인합니다.
 * framework : JUnit Jupiter
 * @author jhon S, sungkibum
 */
public class SolutionDtoTest {

	/** 테스트 객체 SolutionData */
	private SolutionData mockSolutionData;

	/** 독립적 실행을 위해 테스트 실행 전, mockSolutionEvent mockSolutionData 초기화 메서드 */
	@BeforeEach
	@DisplayName("테스트 실행 전, mockSolutionEvent mockSolutionData 초기화")
	void setUp() {
		// SolutionEvent 객체 생성
		SolutionEvent mockSolutionEvent = SolutionEvent.builder()
			.sessionId("session1")
			.pageUrl("https://example.com")
			.type("click")
			.clientX(100)
			.clientY(200)
			.element("button")
			.timestamp(LocalDateTime.now())
			.browserWidth(1024)
			.build();

		// SolutionData 객체 생성 (SolutionEvent 포함)
		mockSolutionData = SolutionData.builder()
			.serialNumber("11db0706-4879-463a-a4d7-f7c347668cc6")
			.solutionEvents(List.of(mockSolutionEvent))
			.build();
	}

	/**
	 * SolutionData 객체를 SolutionRequestDto로 변환
	 * serialNumber와 eventData의 값이 정확히 매핑되는지 확인합니다.
	 */
	@Test
	@DisplayName("SolutionData 객체를 SolutionRequestDto로 변환")
	void testFromSolutionData() {
		SolutionRequestDto solutionRequestDto = SolutionRequestDto.from(mockSolutionData);

		// solutionRequestDto의 값 검증
		assertNotNull(solutionRequestDto, "SolutionRequestDto 객체는 null이 아니어야 합니다.");
		assertEquals("11db0706-4879-463a-a4d7-f7c347668cc6", solutionRequestDto.serialNumber(), "serialNumber 값이 일치해야 합니다.");
		assertNotNull(solutionRequestDto.eventData(), "eventData 리스트는 null이 아니어야 합니다.");
		assertEquals(1, solutionRequestDto.eventData().size(), "eventData 리스트의 크기가 1이어야 합니다.");

		// eventData의 요소 검증
		SolutionRequestDto.EventDataDto eventData = solutionRequestDto.eventData().get(0);
		assertEquals("session1", eventData.sessionId(), "sessionId 값이 일치해야 합니다.");
		assertEquals("click", eventData.type(), "type 값이 일치해야 합니다.");
		assertEquals(100, eventData.x(), "x 값이 일치해야 합니다.");
		assertEquals(200, eventData.y(), "y 값이 일치해야 합니다.");
		assertEquals("button", eventData.element(), "element 값이 일치해야 합니다.");
		assertEquals("http://example.com", eventData.pageUrl(), "pageUrl 값이 일치해야 합니다.");
		assertEquals(1024, eventData.broswerWidth(), "browserWidth 값이 일치해야 합니다.");
	}
}
