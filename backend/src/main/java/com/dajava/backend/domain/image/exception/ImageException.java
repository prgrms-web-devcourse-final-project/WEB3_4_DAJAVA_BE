package com.dajava.backend.domain.image.exception;

import com.dajava.backend.global.exception.ErrorCode;

public class ImageException extends RuntimeException {
	public final ErrorCode errorCode;

	public ImageException(final ErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
	}
}
