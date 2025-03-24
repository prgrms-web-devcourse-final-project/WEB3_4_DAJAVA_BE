package com.dajava.backend.domain.register.exception;

import com.dajava.backend.global.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SolutionException extends RuntimeException {
	public final ErrorCode errorCode;
}
