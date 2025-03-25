package com.dajava.backend.domain.register.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(description = "솔루션 리스트 조회 요청 DTO")
public record RegistersInfoRequest(
	@Schema(description = "페이지 크기", example = "10", required = true)
	@NonNull
	Integer pageSize,

	@Schema(description = "페이지 번호", example = "2", required = true)
	@NonNull
	Integer pageNum
) {
}
