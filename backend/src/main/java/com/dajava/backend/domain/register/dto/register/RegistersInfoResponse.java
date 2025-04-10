package com.dajava.backend.domain.register.dto.register;

import java.util.List;

import com.dajava.backend.domain.register.RegisterInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.NonNull;

@Schema(description = "솔루션 리스트 조회 응답 DTO")
@Builder
public record RegistersInfoResponse(

	@Schema(description = "전체 페이지 개수", required = true)
	@NonNull
	Long totalPages,

	@Schema(description = "전체 솔루션 개수", required = true)
	@NonNull
	Long totalElements,

	@Schema(description = "요청한 페이지 크기", required = true)
	@NonNull
	Integer pageSize,

	@Schema(description = "요청한 페이지 번호", required = true)
	@NonNull
	Integer pageNum,

	@Schema(description = "등록된 솔루션(RegisterInfo)을 리스트 형태로 반환", required = true)
	@NonNull
	List<RegisterInfo> registerInfos
) {
	public static RegistersInfoResponse create(
		List<RegisterInfo> registerInfos,
		long registersSize,
		long totalPages,
		Integer pageNum,
		Integer pageSize
	) {
		return RegistersInfoResponse.builder()
			.registerInfos(registerInfos)
			.totalPages(totalPages)
			.pageNum(pageNum)
			.pageSize(pageSize)
			.totalElements(registersSize)
			.build();
	}
}
