package com.dajava.backend.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.domain.home.exception.SampleException;
import com.dajava.backend.domain.register.exception.AdminException;
import com.dajava.backend.domain.register.exception.RegisterException;

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

	@ExceptionHandler(RegisterException.class)
	public ResponseEntity<ErrorData> handleSolutionException(RegisterException e) {
		log.error("Solution Error, message : {}", e.getMessage());
		return ResponseEntity.status(e.errorCode.getHttpStatus())
			.body(ErrorData.create(e.getMessage()));
	}

	@ExceptionHandler(PointerEventException.class)
	public ResponseEntity<ErrorData> handlePointerEventException(PointerEventException e) {
		log.error("Pointer Event Error, message : {}", e.getMessage());
		return ResponseEntity.status(e.errorCode.getHttpStatus())
			.body(ErrorData.create(e.getMessage()));
	}


	@ExceptionHandler(AdminException.class)
	public ResponseEntity<ErrorData> handleAdminException(AdminException e) {
		log.error("Admin Error, message : {}", e.getMessage());
		return ResponseEntity.status(e.errorCode.getHttpStatus())
			.body(ErrorData.create(e.getMessage()));
	}
}
