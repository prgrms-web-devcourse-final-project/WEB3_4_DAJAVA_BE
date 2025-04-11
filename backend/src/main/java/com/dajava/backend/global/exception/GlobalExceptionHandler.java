package com.dajava.backend.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.domain.heatmap.exception.HeatmapException;
import com.dajava.backend.domain.register.exception.AdminException;
import com.dajava.backend.domain.register.exception.RegisterException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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

	@ExceptionHandler(HeatmapException.class)
	public ResponseEntity<ErrorData> handleHeatmapException(HeatmapException e) {
		log.error("Heatmap Error, message : {}", e.getMessage());
		return ResponseEntity.status(e.errorCode.getHttpStatus())
			.body(ErrorData.create(e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorData> handleValidationException(MethodArgumentNotValidException e) {
		log.warn("Validation Error: {}", e.getMessage());

		// 첫 번째 필드 오류만 응답에 담음 (전체 리스트로 줄 수도 있음)
		String errorMessage = e.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
			.orElse("잘못된 요청입니다.");

		return ResponseEntity.badRequest().body(ErrorData.create(errorMessage));
	}
}
