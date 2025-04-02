package com.dajava.backend.domain.solution.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;

/**
 * <p>{@code SolutionResponseDto}는 AI Gemini에게 솔루션을 반환받기 위한 DTO입니다.</p>
 * <p>{@code text} 반드시 값이 존재해야 하며, {@link NotBlank} 어노테이션을 통해 검증됩니다.
 * 또한, 긴 텍스트 데이터를 저장할 수 있도록 {@link Lob}이 적용되었습니다.</p>
 */
public record SolutionResponse(
	@Lob
	@NotBlank(message = "솔루션 내용은 비어 있을 수 없습니다.")
	String text
) {}
