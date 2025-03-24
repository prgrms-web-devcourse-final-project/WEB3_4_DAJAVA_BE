package com.dajava.backend.domain.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "솔루션 생성 응답 DTO")
@Builder
public record SolutionCreateResponse(
	@Schema(description = "생성된 솔루션의 일련번호", example = "UUID....")
	String serialNumber
) {
}
