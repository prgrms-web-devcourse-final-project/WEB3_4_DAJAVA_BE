package com.dajava.backend.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

	SAMPLE_ERROR(HttpStatus.BAD_REQUEST, "Sample error");

	private final HttpStatus httpStatus;
	private final String description;
}
