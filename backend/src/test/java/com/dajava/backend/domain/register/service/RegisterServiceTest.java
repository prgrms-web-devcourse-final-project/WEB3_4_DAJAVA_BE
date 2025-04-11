package com.dajava.backend.domain.register.service;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dajava.backend.domain.email.AsyncEmailSender;
import com.dajava.backend.domain.image.service.pageCapture.FileStorageService;
import com.dajava.backend.domain.register.dto.pageCapture.PageCaptureRequest;
import com.dajava.backend.domain.register.dto.pageCapture.PageCaptureResponse;
import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterCreateResponse;
import com.dajava.backend.domain.register.dto.register.RegisterModifyRequest;
import com.dajava.backend.domain.register.dto.register.RegistersInfoRequest;
import com.dajava.backend.domain.register.dto.register.RegistersInfoResponse;
import com.dajava.backend.domain.register.entity.PageCaptureData;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;

@SpringBootTest
@Transactional
class RegisterServiceTest {

	@Autowired
	RegisterService service;

	@Autowired
	RegisterRepository repository;

	@MockitoBean
	FileStorageService fileStorageService;

	@MockitoBean
	private AsyncEmailSender asyncEmailSender;

	@BeforeEach
	void setUp() throws Exception {
		// Register Repository 전부 삭제
		repository.deleteAll();

		// email 관련 작업 실제로 송신이 이루어지지 않게 방지
		doNothing().when(asyncEmailSender).sendEmail(anyString(), anyString(), anyString());
	}

	@Test
	@DisplayName("솔루션 생성")
	public void t1() {
		RegisterCreateRequest request = new RegisterCreateRequest(
			"example@example.com",
			"password123!",
			"localhost:3000/test123",
			LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1L),
			LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
		);

		RegisterCreateResponse response = service.createRegister(request);
		Assertions.assertNotNull(response);
		Assertions.assertEquals(1, repository.findAll().size());
	}

	@Test
	@DisplayName("솔루션 수정")
	public void t2() {
		t1();

		Register solution = repository.findAll().get(0);
		Long solutionId = solution.getId();
		RegisterModifyRequest request = new RegisterModifyRequest(
			solution.getEndDate().plusDays(3).withHour(0).withMinute(0).withSecond(0).withNano(0), "abcd"
		);
		int curDuration = solution.getDuration();

		service.modifySolution(request, solutionId);
		Register modifiedSolution = repository.findById(solutionId).get();

		Assertions.assertNotNull(modifiedSolution);
		Assertions.assertEquals(curDuration + 72, modifiedSolution.getDuration());
	}

	@Test
	@DisplayName("솔루션 리스트 조회")
	public void t3() {
		for (int i = 0; i < 10; i++) {
			RegisterCreateRequest request = new RegisterCreateRequest(
				"example@example.com",
				"password123!",
				"localhost:3000/test123" + i,
				LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1L),
				LocalDateTime.now().plusDays(7).withHour(0).withMinute(0).withSecond(0).withNano(0)
			);
			service.createRegister(request);
		}

		RegistersInfoRequest request = new RegistersInfoRequest(5, 1);
		RegistersInfoResponse registerList = service.getRegisterList(request);
		Assertions.assertNotNull(registerList);
		Assertions.assertEquals(5, registerList.registerInfos().size());
	}

	@Test
	@DisplayName("캡쳐 데이터 Post - 성공 (새 이미지)")
	public void t4() throws IOException {
		// Given
		t1();

		Register register = repository.findAll().get(0);
		String serialNumber = register.getSerialNumber();
		String pageUrl = "http://localhost:3000/test";

		// 유효한 MultipartFile 생성
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			MediaType.IMAGE_PNG_VALUE,
			"테스트 이미지 데이터".getBytes()
		);

		// fileStorageService.storeFile() 호출 시 경로 반환하도록 모킹
		String dynamicFileName = UUID.randomUUID().toString() + ".png";
		when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn(dynamicFileName);

		// When: 페이지 캡쳐 데이터 업데이트 메서드 호출
		PageCaptureRequest request = new PageCaptureRequest(serialNumber, pageUrl, imageFile);
		PageCaptureResponse response = service.createPageCapture(request);

		// Then: 응답 확인
		Assertions.assertTrue(response.success());
		Assertions.assertEquals("페이지 캡쳐 데이터가 성공적으로 저장되었습니다.", response.message());
		Assertions.assertEquals(dynamicFileName, response.captureFileName());

		// Repository 에서 해당 Register 및 캡쳐 데이터 확인
		Optional<Register> updatedRegister = repository.findBySerialNumber(serialNumber);
		Assertions.assertTrue(updatedRegister.isPresent());

		Register modifiedRegister = updatedRegister.get();
		Assertions.assertFalse(modifiedRegister.getCaptureData().isEmpty());
		Assertions.assertEquals(pageUrl, modifiedRegister.getCaptureData().get(0).getPageUrl());
		Assertions.assertEquals(dynamicFileName, modifiedRegister.getCaptureData().get(0).getCaptureFileName());

		// fileStorageService.storeFile() 호출 확인
		verify(fileStorageService, times(1)).storeFile(any(MultipartFile.class));
	}

	@Test
	@DisplayName("캡쳐 데이터 Post - 성공 (기존 이미지 오버라이드)")
	public void t5() throws IOException {
		// Given: Register 객체 생성 (t1 메서드를 통해)
		t1();
		Register register = repository.findAll().get(0);
		String serialNumber = register.getSerialNumber();
		String pageUrl = "http://localhost:3000/test";

		// 기존 캡쳐 데이터 추가
		String existingFileName = "existingImage.png";
		PageCaptureData existingData = PageCaptureData.builder()
			.pageUrl(pageUrl)
			.captureFileName(existingFileName)
			.register(register)
			.build();
		register.getCaptureData().add(existingData);
		repository.save(register);

		MockMultipartFile imageFile = new MockMultipartFile(
			"file",
			"updated-image.png",
			MediaType.IMAGE_PNG_VALUE,
			"업데이트된 테스트 이미지 데이터".getBytes()
		);

		// fileStorageService.updateFile() 모킹: 실제 구현 로직이 기존 파일 이름을 그대로 반환하도록 함
		when(fileStorageService.updateFile(any(MultipartFile.class), any(PageCaptureData.class)))
			.thenReturn(existingFileName);

		// When: 페이지 캡쳐 데이터 업데이트 메서드 호출
		PageCaptureRequest request = new PageCaptureRequest(serialNumber, pageUrl, imageFile);
		PageCaptureResponse response = service.createPageCapture(request);

		// Then: 응답 값 검증
		Assertions.assertTrue(response.success(), "응답 결과는 성공이어야 합니다.");
		Assertions.assertEquals("페이지 캡쳐 데이터가 성공적으로 저장되었습니다.", response.message());
		// 업데이트 후에도 기존 파일 이름이 그대로 반환되어야 함
		Assertions.assertEquals(existingFileName, response.captureFileName());

		// Then
		Optional<Register> updatedRegister = repository.findBySerialNumber(serialNumber);
		Assertions.assertTrue(updatedRegister.isPresent(), "업데이트된 Register가 존재해야 합니다.");
		Register modifiedRegister = updatedRegister.get();
		Assertions.assertFalse(modifiedRegister.getCaptureData().isEmpty(), "캡쳐 데이터가 존재해야 합니다.");
		Assertions.assertEquals(pageUrl, modifiedRegister.getCaptureData().get(0).getPageUrl());
		Assertions.assertEquals(existingFileName, modifiedRegister.getCaptureData().get(0).getCaptureFileName());

		// fileStorageService.updateFile() 호출 횟수 및 인자 검증
		verify(fileStorageService, times(1)).updateFile(any(MultipartFile.class), any(PageCaptureData.class));
	}
}
