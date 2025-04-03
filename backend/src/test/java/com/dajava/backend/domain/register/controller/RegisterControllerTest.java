package com.dajava.backend.domain.register.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterModifyRequest;
import com.dajava.backend.domain.register.dto.register.RegistersInfoRequest;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.register.service.RegisterCacheService;
import com.dajava.backend.domain.register.service.RegisterService;
import com.dajava.backend.global.exception.ErrorCode;
import com.dajava.backend.global.utils.PasswordUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegisterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private RegisterService registerService;

	@Autowired
	private RegisterCacheService registerCacheService;

	@Autowired
	private RegisterRepository registerRepository;

	@Value("${custom.adminCode}")
	private String adminCode;

	private String cookieKey = "admin_auth";
	private String cookieValue; // 초기화하지 않음

	@BeforeEach
	void setUp() throws Exception {
		registerRepository.deleteAll();
		// 테스트 실행 전에 cookieValue 초기화
		cookieValue = PasswordUtils.hashPassword(adminCode);
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
			now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1L),
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
			now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1L),
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
				.cookie(new Cookie(cookieKey, cookieValue))
				.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
				.content(objectMapper.writeValueAsString(new RegisterModifyRequest(
					LocalDateTime.now().plusDays(3L).withHour(0).withMinute(0).withSecond(0).withNano(0),
					"abcde"))))
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
				.cookie(new Cookie(cookieKey, cookieValue))
				.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		resultActions.andExpect(jsonPath("$.registerInfos").isNotEmpty());
	}

	@Test
	@DisplayName("솔루션 캡쳐 데이터 저장 : 성공")
	void t7() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.now();
		RegisterCreateRequest request = new RegisterCreateRequest(
			"test@example.com",
			"password123",
			"localhost:3000/test",
			now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1L),
			now.plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		// when & then
		MvcResult result = mockMvc.perform(post("/v1/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn();

		// Given Second
		String createResponse = result.getResponse().getContentAsString();
		JsonNode createJson = objectMapper.readTree(createResponse);
		String serialNumber = createJson.get("serialNumber").asText();

		// 테스트용 이미지 파일 생성
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			"image/png",
			"테스트 이미지 데이터".getBytes()
		);

		registerCacheService.refreshCacheAll();

		// When & Then Second
		mockMvc.perform(multipart("/v1/register/" + serialNumber + "/page-capture")
				.file(imageFile))
			.andExpect(status().isOk())
			.andExpect(content().string("페이지 캡쳐 데이터가 성공적으로 업데이트되었습니다."));

		Register updateRegister = registerRepository.findBySerialNumber(serialNumber)
			.orElseThrow(() -> new RegisterException(ErrorCode.REGISTER_NOT_FOUND));

		// 파일 URL 형식 검증 - UUID 패턴으로 시작하는지 확인
		assertNotNull(updateRegister.getPageCapture());
		assertTrue(updateRegister.getPageCapture().startsWith("/page-capture/"));
	}

	@Test
	@DisplayName("솔루션 캡쳐 데이터 저장 : 실패, 사유 : 잘못된 식별자")
	void t8() throws Exception {
		// given
		LocalDateTime now = LocalDateTime.now();
		RegisterCreateRequest request = new RegisterCreateRequest(
			"test@example.com",
			"password123",
			"localhost:3000/test",
			now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1L),
			now.plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		// when & then
		mockMvc.perform(post("/v1/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andReturn();

		// Given Second
		String wrongSerialNumber = "wrongSerialNumber";

		// 테스트용 이미지 파일 생성
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			"image/png",
			"테스트 이미지 데이터".getBytes()
		);

		registerCacheService.refreshCacheAll();

		// When & Then Second
		mockMvc.perform(multipart("/v1/register/" + wrongSerialNumber + "/page-capture")
				.file(imageFile))
			.andExpect(status().isUnauthorized());
	}
}
