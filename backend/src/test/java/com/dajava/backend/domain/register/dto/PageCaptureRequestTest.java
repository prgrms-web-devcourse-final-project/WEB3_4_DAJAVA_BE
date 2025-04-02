package com.dajava.backend.domain.register.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.register.dto.capture.PageCaptureRequest;
import com.dajava.backend.domain.register.exception.RegisterException;

public class PageCaptureRequestTest {

	@Test
	@DisplayName("1. 공백 인자 전달 시 실패 테스트")
	void t1() {
		RegisterException exception = assertThrows(RegisterException.class, () -> {
			new PageCaptureRequest(List.of());
		});
		assertEquals("pageCapture 리스트는 공백이거나 null 일 수 없습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("2. null 전달 시 실패 테스트")
	void t2() {
		RegisterException exception = assertThrows(RegisterException.class, () -> {
			new PageCaptureRequest(null);
		});
		assertEquals("pageCapture 리스트는 공백이거나 null 일 수 없습니다.", exception.getMessage());
	}
}
