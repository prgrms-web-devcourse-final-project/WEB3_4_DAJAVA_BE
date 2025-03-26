package com.dajava.backend.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	// Solution
	INVALID_SOLUTION_REQUEST(HttpStatus.BAD_REQUEST, "요청 값에 대한 유효성 검증에 실패하였습니다"),
	ALREADY_REGISTER_URL(HttpStatus.BAD_REQUEST, "이미 등록되어 있습니다"),

	// Sample
	SAMPLE_ERROR(HttpStatus.BAD_REQUEST, "Sample error");

	private final HttpStatus httpStatus;
	private final String description;
}
