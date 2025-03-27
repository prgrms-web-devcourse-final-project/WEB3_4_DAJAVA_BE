package com.dajava.backend.global.exception;

import static com.dajava.backend.domain.register.constant.RegisterConstant.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	// Solution
	INVALID_REGISTER_REQUEST(HttpStatus.BAD_REQUEST, "요청 값에 대한 유효성 검증에 실패하였습니다"),
	ALREADY_REGISTER_URL(HttpStatus.BAD_REQUEST, "이미 등록되어 있습니다"),
	SOLUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 솔루션이 존재하지 않습니다"),
	MODIFY_DATE_EXCEEDED(HttpStatus.BAD_REQUEST, "현재 종료일로부터 " + DEFAULT_MODIFY_MAX_DATE + "일을 초과할 수 없습니다."),

	// Sample
	SAMPLE_ERROR(HttpStatus.BAD_REQUEST, "Sample error");

	private final HttpStatus httpStatus;
	private final String description;
}
