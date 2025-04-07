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
import org.springframework.web.multipart.MultipartFile;

import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterCreateResponse;
import com.dajava.backend.domain.register.dto.register.RegisterModifyRequest;
import com.dajava.backend.domain.register.dto.register.RegistersInfoRequest;
import com.dajava.backend.domain.register.dto.register.RegistersInfoResponse;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.register.service.pageCapture.FileStorageService;

@SpringBootTest
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
	@DisplayName("캡쳐 데이터 Post - 성공")
	public void t4() throws IOException {
		// Given
		t1();

		Register register = repository.findAll().get(0);
		String serialNumber = register.getSerialNumber();

		// 유효한 MultipartFile 생성
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			MediaType.IMAGE_PNG_VALUE,
			"테스트 이미지 데이터".getBytes()
		);

		// fileStorageService.storeFile()가 호출될 때 UUID 기반 경로를 반환하도록 모킹 (예: "/page-capture/UUID.png")
		String dynamicFilePath = "/page-capture/" + UUID.randomUUID().toString() + ".png";
		when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn(dynamicFilePath);

		// When: 페이지 캡쳐 데이터 업데이트 메서드를 호출
		String result = service.createPageCapture(serialNumber, imageFile);

		// Then: Repository 에서 해당 Register 객체를 조회하여 업데이트가 반영되었는지 확인
		Optional<Register> modifiedSolution = repository.findBySerialNumber(serialNumber);
		Assertions.assertTrue(modifiedSolution.isPresent(),
			"해당 serialNumber 를 가진 Register 객체가 존재해야 합니다.");

		String pageCapturePath = modifiedSolution.get().getPageCapture();
		String regex = "^/page-capture/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}\\.png$"; // UUID Regex
		Assertions.assertTrue(pageCapturePath.matches(regex),
			"파일 경로가 기대하는 패턴과 일치하지 않습니다. 받은 경로: " + pageCapturePath);

		Assertions.assertEquals("페이지 캡쳐 데이터가 성공적으로 업데이트되었습니다.", result);
	}

	@Test
	@DisplayName("캡쳐 데이터 Post - 실패 (이미 캡쳐 데이터 존재)")
	public void t5() throws IOException {
		// Given
		t1();
		Register register = repository.findAll().get(0);
		String existingFilePath = "/page-capture/existingImage.png";
		register.updatePageCapture(existingFilePath);
		repository.save(register);

		String serialNumber = register.getSerialNumber();

		// 유효한 MultipartFile 생성 (실제 파일 저장은 진행되지 않아야 함)
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			MediaType.IMAGE_PNG_VALUE,
			"테스트 이미지 데이터".getBytes()
		);

		// When: 페이지 캡쳐 데이터 업데이트 메서드를 호출하면,
		// 이미 pageCapture 가 존재하므로 파일 저장을 시도하지 않고 실패 메시지를 반환해야 함
		String result = service.createPageCapture(serialNumber, imageFile);

		// Then
		Assertions.assertEquals("이미 페이지 캡쳐 데이터가 존재합니다.", result);

		// fileStorageService.storeFile() 가 호출되지 않았음을 검증합니다.
		verify(fileStorageService, times(0)).storeFile(any(MultipartFile.class));
	}

}
