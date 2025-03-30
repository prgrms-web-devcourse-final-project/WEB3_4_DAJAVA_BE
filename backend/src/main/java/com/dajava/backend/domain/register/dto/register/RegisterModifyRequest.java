package com.dajava.backend.domain.register.dto.register;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(description = "솔루션 수정 요청 DTO")
public record RegisterModifyRequest(
	@Schema(description = "새로 갱신되는 솔루션 종료 일시", example = "2025-03-28T12:00:00", required = true)
	@NonNull
	LocalDateTime solutionCompleteDate
) {
}
