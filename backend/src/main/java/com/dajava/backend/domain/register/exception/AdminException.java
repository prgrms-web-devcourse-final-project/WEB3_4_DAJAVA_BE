package com.dajava.backend.domain.register.exception;

import com.dajava.backend.global.exception.ErrorCode;

public class AdminException extends RuntimeException {
	public final ErrorCode errorCode;

	public AdminException(final ErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
	}
}
