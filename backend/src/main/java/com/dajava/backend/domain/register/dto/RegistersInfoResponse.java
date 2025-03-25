package com.dajava.backend.domain.register.dto;

import java.util.List;

import com.dajava.backend.domain.register.RegisterInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;

@Schema(description = "솔루션 리스트 조회 응답 DTO")
@Builder
public record RegistersInfoResponse(
	@Schema(description = "등록된 솔루션(RegisterInfo)을 리스트 형태로 반환", required = true)
	@NonNull
	List<RegisterInfo> registers
) {
	public static RegistersInfoResponse create() {
		return RegistersInfoResponse.builder().build();
	}
}
