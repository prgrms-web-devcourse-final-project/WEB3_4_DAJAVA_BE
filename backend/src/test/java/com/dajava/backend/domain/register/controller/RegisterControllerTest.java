package com.dajava.backend.domain.register.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.dajava.backend.domain.register.dto.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.RegisterModifyRequest;
import com.dajava.backend.domain.register.dto.RegistersInfoRequest;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.register.service.RegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class RegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RegisterService registerService;

	@Autowired
	private RegisterRepository registerRepository;

	@BeforeEach
	void setUp() throws Exception {
		registerRepository.deleteAll();
	}

	@Test
	@DisplayName("솔루션 신청 : 성공")
	void t1() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.now();
		RegisterCreateRequest request = new RegisterCreateRequest(
			"test@example.com",
			"password123",
			"localhost:3000/test",
			now.withHour(0).withMinute(0).withSecond(0).withNano(0),
			now.plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		// when & then
		mockMvc.perform(post("/v1/register")
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
		RegisterCreateRequest request = new RegisterCreateRequest(
			"invalid-email", // 잘못된 이메일
			"password123",
			"localhost:3000/test",
			now.withHour(0).withMinute(0).withSecond(0).withNano(0),
			now.plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		// when & then
		mockMvc.perform(post("/v1/register")
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
		RegisterCreateRequest request = new RegisterCreateRequest(
			"chsan626@gmail.com",
			"password123!",
			"localhost:3000/test123",
			now.withHour(0).withMinute(0).withSecond(0).withNano(0),
			now.plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		// when & then
		mockMvc.perform(post("/v1/register")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		mockMvc.perform(post("/v1/register")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("솔루션 수정 : 성공")
	void t4() throws Exception {
		t1();
		List<Register> all = registerRepository.findAll();
		Register register = all.get(0);

		mockMvc.perform(patch("/v1/register/" + register.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
				.content(objectMapper.writeValueAsString(new RegisterModifyRequest(
					LocalDateTime.now().plusDays(3L).withHour(0).withMinute(0).withSecond(0).withNano(0)))))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("솔루션 조회 : 성공")
	void t6() throws Exception {
		t1();

		RegistersInfoRequest request = new RegistersInfoRequest(
			10, 0
		);

		ResultActions resultActions = mockMvc.perform(get("/v1/registers")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		resultActions.andExpect(jsonPath("$.registerInfos").isNotEmpty());
	}
}
