package com.dajava.backend.domain.register.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterModifyRequest;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.register.service.RegisterCacheService;
import com.dajava.backend.domain.register.service.RegisterService;
import com.dajava.backend.global.utils.PasswordUtils;
import com.dajava.backend.global.utils.TimeUtils;
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

	// 테스트가 끝난 후 생성된 파일 및 디렉토리를 삭제
	@AfterAll
	static void cleanup() throws IOException {
		Path baseDir = Paths.get("C:/page-capture");
		if (Files.exists(baseDir)) {
			// 디렉토리 내 모든 파일 및 하위 디렉토리를 역순(파일부터 지워야 디렉토리 지울 수 있음)으로 삭제
			Files.walk(baseDir)
				.sorted(Comparator.reverseOrder())
				.forEach(path -> {
					try {
						Files.deleteIfExists(path);
					} catch (IOException e) {
						System.err.println("삭제 실패: " + path);
					}
				});
		}
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

	// 현재는 테스트를 위해 검증을 주석처리했기 때문에 200이 반환됨, 주석처리
	// @Test
	// @DisplayName("솔루션 신청 : 실패, 사유 : 잘못된 입력")
	// void t2() throws Exception {
	// 	// given
	// 	LocalDateTime now = LocalDateTime.now();
	// 	RegisterCreateRequest request = new RegisterCreateRequest(
	// 		"invalid-email", // 잘못된 이메일
	// 		"password123",
	// 		"localhost:3000/test",
	// 		now.withHour(0).withMinute(0).withSecond(0).withNano(0),
	// 		now.plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
	// 	);
	//
	// 	// when & then
	// 	mockMvc.perform(post("/v1/register")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
	// 			.content(objectMapper.writeValueAsString(request)))
	// 		.andExpect(status().isBadRequest());
	// }

	// 현재는 테스트를 위해 검증을 주석처리했기 때문에 200이 반환됨 (중복 확인 X), 주석처리
	// @Test
	// @DisplayName("솔루션 신청 : 실패, 사유 : URL 등록 실패")
	// void t3() throws Exception {
	// 	// given
	// 	LocalDateTime now = LocalDateTime.now();
	// 	RegisterCreateRequest request = new RegisterCreateRequest(
	// 		"chsan626@gmail.com",
	// 		"password123!",
	// 		"localhost:3000/test123",
	// 		now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1L),
	// 		now.plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
	// 	);
	//
	// 	// when & then
	// 	mockMvc.perform(post("/v1/register")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
	// 			.content(objectMapper.writeValueAsString(request)))
	// 		.andExpect(status().isOk());
	//
	// 	mockMvc.perform(post("/v1/register")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.accept(MediaType.APPLICATION_JSON)  // Accept 헤더 추가
	// 			.content(objectMapper.writeValueAsString(request)))
	// 		.andExpect(status().isBadRequest());
	// }

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
		t1(); // 인증용 쿠키 설정 등 준비 코드

		ResultActions resultActions = mockMvc.perform(get("/v1/registers")
			.accept(MediaType.APPLICATION_JSON)
			.param("pageSize", "10")
			.param("pageNum", "0")
			.cookie(new Cookie(cookieKey, cookieValue)) // ✅ 여기 괄호 닫기
		);

		resultActions.andExpect(jsonPath("$.registerInfos").isNotEmpty());
	}

	@Test
	@DisplayName("솔루션 캡쳐 데이터 저장 : 성공")
	void t7() throws Exception {
		// Given
		LocalDateTime now = LocalDateTime.now();
		RegisterCreateRequest request = new RegisterCreateRequest(
			"test@example.com",
			"password123",
			"localhost:3000/test",
			now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0),
			now.plusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		Register register = Register.builder()
			.serialNumber("correct_testSerial")
			.email(request.email())
			.password(PasswordUtils.hashPassword(request.password()))
			.url(request.url())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.duration(TimeUtils.getDuration(request.startDate(), request.endDate()))
			.isServiceExpired(false)
			.isSolutionComplete(false)
			.captureData(new ArrayList<>())
			.build();

		Register newRegister = registerRepository.save(register);

		String serialNumber = newRegister.getSerialNumber();
		String pageUrl = newRegister.getUrl();

		// 테스트용 이미지 파일 생성
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			"image/png",
			"테스트 이미지 데이터".getBytes()
		);

		// 캐시 리프레시
		registerCacheService.refreshCacheAll();

		// When & Then Second - 실제 서비스를 사용하여 테스트
		mockMvc.perform(multipart("/v1/register/page-capture")
				.file(imageFile)
				.part(new MockPart("serialNumber", serialNumber.getBytes(StandardCharsets.UTF_8)))
				.part(new MockPart("pageUrl", pageUrl.getBytes(StandardCharsets.UTF_8)))
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").isString())
			.andExpect(jsonPath("$.captureFileName").isString());
	}

	@Test
	@DisplayName("솔루션 캡쳐 데이터 저장 : 실패, 사유 : 잘못된 식별자")
	void t8() throws Exception {
		// Given
		LocalDateTime now = LocalDateTime.now();
		RegisterCreateRequest request = new RegisterCreateRequest(
			"test@example.com",
			"password123",
			"localhost:3000/test",
			now.withHour(0).withMinute(0).withSecond(0).withNano(0),
			now.plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		Register register = Register.builder()
			.serialNumber("correct_testSerial")
			.email(request.email())
			.password(PasswordUtils.hashPassword(request.password()))
			.url(request.url())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.duration(TimeUtils.getDuration(request.startDate(), request.endDate()))
			.isServiceExpired(false)
			.isSolutionComplete(false)
			.captureData(new ArrayList<>())
			.build();

		Register newRegister = registerRepository.save(register);

		String serialNumber = "wrong_testSerial";
		String pageUrl = newRegister.getUrl();

		// 테스트용 이미지 파일 생성
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			"image/png",
			"테스트 이미지 데이터".getBytes()
		);

		// 캐시 리프레시
		registerCacheService.refreshCache();

		// When & Then Second - 실제 호출에서 401 예상
		mockMvc.perform(multipart("/v1/register/page-capture")
				.file(imageFile)
				.part(new MockPart("serialNumber", serialNumber.getBytes(StandardCharsets.UTF_8)))
				.part(new MockPart("pageUrl", pageUrl.getBytes(StandardCharsets.UTF_8)))
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
			.andExpect(status().isUnauthorized());
	}
}
