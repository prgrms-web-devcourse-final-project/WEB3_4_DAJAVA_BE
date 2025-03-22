package com.dajava.backend.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dajava.backend.domain.home.exception.SampleException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(SampleException.class)
	public ResponseEntity<ErrorData> handleSampleException(SampleException e) {
		log.error("Sample Error, message : {}", e.getMessage());
		return ResponseEntity.status(e.errorCode.getHttpStatus())
			.body(ErrorData.create(e.getMessage()));
	}

}
