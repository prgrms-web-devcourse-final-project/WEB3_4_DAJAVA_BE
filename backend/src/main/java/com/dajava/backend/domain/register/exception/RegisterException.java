package com.dajava.backend.domain.register.exception;

import com.dajava.backend.global.exception.ErrorCode;

public class RegisterException extends RuntimeException {
	public final ErrorCode errorCode;

	public RegisterException(final ErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
	}
}
