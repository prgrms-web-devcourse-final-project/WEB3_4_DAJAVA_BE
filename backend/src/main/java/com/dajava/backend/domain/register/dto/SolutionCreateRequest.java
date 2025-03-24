package com.dajava.backend.domain.register.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(description = "솔루션 생성 요청 DTO")
public record SolutionCreateRequest(
	@Schema(description = "사용자 이메일", example = "user@example.com", required = true)
	@NonNull
	String email,

	@Schema(description = "비밀번호", example = "password123", required = true)
	@NonNull
	String password,

	@Schema(description = "솔루션 URL", example = "localhost:3000/myPage", required = true)
	@NonNull
	String url,

	@Schema(description = "솔루션 시작 일시", example = "2025-03-21T12:00:00", required = true)
	@NonNull
	LocalDateTime startDate,

	@Schema(description = "솔루션 종료 일시", example = "2025-03-28T12:00:00", required = true)
	@NonNull
	LocalDateTime endDate
) {

	@Override
	public String toString() {
		return "솔루션 신청 정보: "
			+ "[email=" + email + ", "
			+ "password=" + password + ", "
			+ "url=" + url + ", "
			+ "startDate=" + startDate + ", "
			+ "endDate=" + endDate + "]";
	}
}
