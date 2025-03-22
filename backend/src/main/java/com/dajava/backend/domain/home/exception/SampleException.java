package com.dajava.backend.domain.home.exception;

import com.dajava.backend.global.exception.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SampleException extends RuntimeException {
	public final ErrorCode errorCode;
}
