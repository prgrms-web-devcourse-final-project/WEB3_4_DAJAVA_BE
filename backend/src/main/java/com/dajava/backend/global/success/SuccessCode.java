package com.dajava.backend.global.success;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode{
	// Solution
	SOLUTION_SERIAL_NUMBER_SUCCESS(HttpStatus.OK, Boolean.TRUE, 200, "시리얼 넘버 데이터 찾았습니다."),
	SOLUTION_REQUEST_SUCCESS(HttpStatus.OK, true, 200, "솔루션이 요청되었습니다.");
	private final HttpStatus httpStatus;
	private final Boolean isSuccess;
	private final int code;
	private final String message;
}
