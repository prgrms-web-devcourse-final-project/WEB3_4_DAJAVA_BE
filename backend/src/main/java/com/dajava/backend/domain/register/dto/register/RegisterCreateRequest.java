package com.dajava.backend.domain.register.dto.register;

import java.time.LocalDateTime;

import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.global.exception.ErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@Schema(description = "솔루션 생성 요청 DTO")
public record RegisterCreateRequest(
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

	/**
	 * URL에 프로토콜 정보가 없다면 https:// 을 접두사로 추가한 새로운 RegisterCreateRequest 객체 반환
	 */
	public RegisterCreateRequest withNormalizedUrl() {
		if (url == null || url.isBlank()) {
			throw new RegisterException(ErrorCode.REGISTER_URL_EMPTY);
		}

		return new RegisterCreateRequest(email, password, "https://" + url, startDate, endDate);
	}

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
