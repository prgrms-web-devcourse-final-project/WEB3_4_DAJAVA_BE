package com.dajava.backend.domain.register.dto.register;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;

/**
 * <p>{@code SolutionResponseDto}는 클라이언트에서 솔루션을 반환 받기 위한 DTO입니다.</p>
 * <p>{@code text} 반드시 값이 존재해야 하며, {@link NotBlank} 어노테이션을 통해 검증됩니다.
 * 또한, 긴 텍스트 데이터를 저장할 수 있도록 {@link Lob}이 적용되었습니다.</p>
 */
public record RegisterCheckResponse(
	@Lob
	@NotBlank(message = "해당 솔루션이 존재해야 합니다.")
	Boolean isExist
) {}
