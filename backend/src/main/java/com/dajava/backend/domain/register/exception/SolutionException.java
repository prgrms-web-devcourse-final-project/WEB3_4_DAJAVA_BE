package com.dajava.backend.domain.register.exception;

import com.dajava.backend.global.exception.ErrorCode;

public class SolutionException extends RuntimeException {
	public final ErrorCode errorCode;

	public SolutionException(final ErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
	}
}
