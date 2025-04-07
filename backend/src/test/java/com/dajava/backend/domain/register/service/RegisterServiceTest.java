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
import com.dajava.backend.domain.register.service.pageCapture.FileStorageService;

@SpringBootTest
@Transactional
class RegisterServiceTest {

	@Autowired
	RegisterService service;

	@Autowired
	RegisterRepository repository;

	@MockitoBean
	FileStorageService fileStorageService;

	@BeforeEach
	void setUp() throws Exception {
		repository.deleteAll();
	}

	@Test
	@DisplayName("솔루션 생성")
	public void t1() {
		RegisterCreateRequest request = new RegisterCreateRequest(
			"chsan626@gmail.com",
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
				"chsan626@gmail.com",
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
		;
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
		String dynamicFilePath = "/page-capture/" + UUID.randomUUID().toString() + ".png";
		when(fileStorageService.storeFile(eq(pageUrl), any(MultipartFile.class))).thenReturn(dynamicFilePath);

		// When: 페이지 캡쳐 데이터 업데이트 메서드 호출
		PageCaptureRequest request = new PageCaptureRequest(serialNumber, pageUrl, imageFile);
		PageCaptureResponse response = service.createPageCapture(request);

		// Then: 응답 확인
		Assertions.assertTrue(response.success());
		Assertions.assertEquals("페이지 캡쳐 데이터가 성공적으로 저장되었습니다.", response.message());
		Assertions.assertEquals(dynamicFilePath, response.pageCaptureUrl());

		// Repository 에서 해당 Register 및 캡쳐 데이터 확인
		Optional<Register> updatedRegister = repository.findBySerialNumber(serialNumber);
		Assertions.assertTrue(updatedRegister.isPresent());

		Register modifiedRegister = updatedRegister.get();
		Assertions.assertFalse(modifiedRegister.getCaptureData().isEmpty());
		Assertions.assertEquals(pageUrl, modifiedRegister.getCaptureData().get(0).getPageUrl());
		Assertions.assertEquals(dynamicFilePath, modifiedRegister.getCaptureData().get(0).getPageCapturePath());

		// fileStorageService.storeFile() 호출 확인
		verify(fileStorageService, times(1)).storeFile(eq(pageUrl), any(MultipartFile.class));
	}

	@Test
	@DisplayName("캡쳐 데이터 Post - 성공 (기존 이미지 오버라이드)")
	public void t5() throws IOException {
		// Given
		t1();
		Register register = repository.findAll().get(0);
		String serialNumber = register.getSerialNumber();
		String pageUrl = "http://localhost:3000/test";

		// 기존 캡쳐 데이터 추가
		String existingFilePath = "/page-capture/existingImage.png";
		PageCaptureData existingData = PageCaptureData.builder()
			.pageUrl(pageUrl)
			.pageCapturePath(existingFilePath)
			.register(register)
			.build();
		register.getCaptureData().add(existingData);
		repository.save(register);

		// 유효한 MultipartFile 생성
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"updated-image.png",
			MediaType.IMAGE_PNG_VALUE,
			"업데이트된 테스트 이미지 데이터".getBytes()
		);

		// fileStorageService.storeFile() 호출 시 경로 반환하도록 모킹
		String newFilePath = "/page-capture/updatedImage.png";
		when(fileStorageService.storeFile(eq(pageUrl), any(MultipartFile.class), eq(existingFilePath)))
			.thenReturn(newFilePath);

		// When: 페이지 캡쳐 데이터 업데이트 메서드 호출
		PageCaptureRequest request = new PageCaptureRequest(serialNumber, pageUrl, imageFile);
		PageCaptureResponse response = service.createPageCapture(request);

		// Then: 응답 확인
		Assertions.assertTrue(response.success());
		Assertions.assertEquals("페이지 캡쳐 데이터가 성공적으로 저장되었습니다.", response.message());
		Assertions.assertEquals(newFilePath, response.pageCaptureUrl());

		// Repository 에서 해당 Register 및 캡쳐 데이터 확인
		Optional<Register> updatedRegister = repository.findBySerialNumber(serialNumber);
		Assertions.assertTrue(updatedRegister.isPresent());

		Register modifiedRegister = updatedRegister.get();
		Assertions.assertFalse(modifiedRegister.getCaptureData().isEmpty());
		Assertions.assertEquals(pageUrl, modifiedRegister.getCaptureData().get(0).getPageUrl());
		Assertions.assertEquals(newFilePath, modifiedRegister.getCaptureData().get(0).getPageCapturePath());

		// fileStorageService.storeFile() 호출 확인 (기존 파일 경로로)
		verify(fileStorageService, times(1)).storeFile(eq(pageUrl), any(MultipartFile.class), eq(existingFilePath));
	}
}
