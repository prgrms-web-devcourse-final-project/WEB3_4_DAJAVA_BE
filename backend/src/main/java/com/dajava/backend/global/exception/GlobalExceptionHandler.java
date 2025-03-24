package com.dajava.backend.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dajava.backend.domain.home.exception.SampleException;
import com.dajava.backend.domain.register.exception.SolutionException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(SampleException.class)
	public ResponseEntity<ErrorData> handleSampleException(SampleException e) {
		log.error("Sample Error, message : {}", e.getMessage());
		return ResponseEntity.status(e.errorCode.getHttpStatus())
			.body(ErrorData.create(e.getMessage()));
	}

	@ExceptionHandler(SolutionException.class)
	public ResponseEntity<ErrorData> handleSolutionException(SolutionException e) {
		log.error("Solution Error, message : {}", e.getMessage());
		return ResponseEntity.status(e.errorCode.getHttpStatus())
			.body(ErrorData.create(e.getMessage()));
	}
}
