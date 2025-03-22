package com.dajava.backend.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorData {
	private String message;

	public static ErrorData create(String message) {
		return new ErrorData(message);
	}
}
