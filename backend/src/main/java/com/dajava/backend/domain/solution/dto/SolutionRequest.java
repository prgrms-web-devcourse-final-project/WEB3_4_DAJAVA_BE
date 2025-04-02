package com.dajava.backend.domain.solution.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * <p>{@code SolutionRequest}는 솔루션 요청 데이터를 표현하는 DTO입니다.</p>
 * <p>마우스 로그, 스크롤, 클릭 이벤트 로그를 포함합니다.</p>
 */
public record SolutionRequest(
	@NotBlank(message = "시리얼 번호는 필수입니다.") String serialNumber,
	@NotNull(message = "이벤트 데이터 목록은 필수입니다.") List<EventDataDto> eventData
) {

	/**
	 * <p>{@code EventDataDto}는 개별 이벤트 데이터를 나타내는 내부 DTO입니다.</p>
	 * <p>필수 필드는 {@code @NotBlank} 또는 {@code @NotNull} 어노테이션을 사용하여 검증됩니다.</p>
	 */
	public record EventDataDto(
		@NotBlank(message = "세션 ID는 필수입니다.") String sessionId,
		@NotNull(message = "타임스탬프는 필수입니다.") LocalDateTime timestamp,
		@NotBlank(message = "이벤트 타입은 필수입니다.") String type,
		Integer scrollY,
		Integer clientX,
		Integer clientY,
		@NotBlank(message = "대상 요소는 필수입니다.") String element,
		@NotBlank(message = "페이지 URL은 필수입니다.") String pageUrl,
		Integer browserWidth
	) {
		/**
		 * <p>{@code SolutionEvent} 엔티티를 {@code EventDataDto}로 변환하는 정적 메서드입니다.</p>
		 * @param event 변환할 {@code SolutionEvent} 객체
		 * @return 변환된 {@code EventDataDto} 객체
		 */
		public static EventDataDto from(SolutionEvent event) {
			return new EventDataDto(
				event.getSessionId(),
				event.getTimestamp(),
				event.getType(),
				event.getScrollY(),
				event.getClientX(),
				event.getClientY(),
				event.getElement(),
				event.getPageUrl(),
				event.getBrowserWidth()
			);
		}
	}

	/**
	 * <p>{@code SolutionData} 엔티티를 {@code SolutionRequest} DTO로 변환하는 정적 메서드입니다.</p>
	 * @param solutionData 변환할 {@code SolutionData} 객체
	 * @return 변환된 {@code SolutionRequest} 객체
	 */
	public static SolutionRequest from(SolutionData solutionData) {
		List<EventDataDto> eventDataDtos = solutionData.getSolutionEvents()
			.stream()
			.map(EventDataDto::from)
			.collect(Collectors.toList());

		return new SolutionRequest(solutionData.getSerialNumber(), eventDataDtos);
	}
}
