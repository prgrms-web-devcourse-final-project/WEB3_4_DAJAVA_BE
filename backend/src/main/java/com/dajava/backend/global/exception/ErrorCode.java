package com.dajava.backend.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	// Register
	INVALID_REGISTER_REQUEST(HttpStatus.BAD_REQUEST, "요청 값에 대한 유효성 검증에 실패하였습니다"),
	ALREADY_REGISTER_URL(HttpStatus.BAD_REQUEST, "이미 등록되어 있습니다"),


	// Solution
	SERIAL_NUMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "시리얼 넘버가 없습니다."),
	SOLUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "솔루션을 찾을 수 없습니다."),
	SOLUTION_TEXT_EMPTY(HttpStatus.BAD_REQUEST, "솔루션 요청할 데이터가 없습니다."),
	SOLUTION_PARSING_ERROR(HttpStatus.BAD_REQUEST, "JSON 파싱에 오류가 발생했습니다."),
	SOLUTION_RESPONSE_ERROR(HttpStatus.BAD_REQUEST, " 응답 처리 중 오류가 발생했습니다."),

	// Sample
	SAMPLE_ERROR(HttpStatus.BAD_REQUEST, "Sample error");

	private final HttpStatus httpStatus;
	private final String description;
}
