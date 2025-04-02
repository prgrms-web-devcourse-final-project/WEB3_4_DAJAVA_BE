package com.dajava.backend.domain.register.dto.capture;

import java.util.List;

import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.global.exception.ErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;

public record PageCaptureRequest(
	@Schema(description = "pageCapture 의 분리된 데이터 String 리스트",
		example = "[example1, example2, example3]",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	List<String> pageCapture
) {
	public PageCaptureRequest {
		if (pageCapture == null || pageCapture.isEmpty()) {
			throw new RegisterException(ErrorCode.INVALID_PAGE_CAPTURE);
		}
	}
}
