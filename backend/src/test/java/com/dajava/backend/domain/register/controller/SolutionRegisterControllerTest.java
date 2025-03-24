package com.dajava.backend.domain.register.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.dajava.backend.domain.register.dto.SolutionCreateRequest;
import com.dajava.backend.domain.register.service.SolutionRegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SolutionRegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private SolutionRegisterService solutionRegisterService;

	@Test
	@DisplayName("솔루션 신청 : 성공")
	void t1() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.now();
		SolutionCreateRequest request = new SolutionCreateRequest(
			"test@example.com",
			"password123",
			"localhost:3000/test",
			now,
			now.plusDays(7)
		);

		// when & then
		mockMvc.perform(post("/v1/solution")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.serialNumber").isNotEmpty());
	}

	@Test
	@DisplayName("솔루션 신청 : 실패, 사유 : 잘못된 입력")
	void t2() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.now();
		SolutionCreateRequest request = new SolutionCreateRequest(
			"invalid-email", // 잘못된 이메일
			"password123",
			"localhost:3000/test",
			now,
			now.plusDays(7)
		);

		// when & then
		mockMvc.perform(post("/v1/solution")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("솔루션 신청 : 실패, 사유 : URL 등록 실패")
	void t3() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.now();
		SolutionCreateRequest request = new SolutionCreateRequest(
			"chsan626@gmail.com",
			"password123!",
			"localhost:3000/test123",
			now,
			now.plusDays(7)
		);

		// when & then
		mockMvc.perform(post("/v1/solution")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		mockMvc.perform(post("/v1/solution")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}
}
