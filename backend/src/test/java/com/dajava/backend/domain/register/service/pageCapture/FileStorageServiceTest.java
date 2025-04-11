package com.dajava.backend.domain.register.service.pageCapture;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import com.dajava.backend.domain.image.service.pageCapture.FileStorageService;
import com.dajava.backend.domain.register.entity.PageCaptureData;
import com.dajava.backend.domain.register.repository.PageCaptureDataRepository;

@SpringBootTest
public class FileStorageServiceTest {

	@Value("${image.path}")
	String path;

	// 생성자 주입 대신 필드에 바로 @Autowired 처리합니다.
	@Autowired
	private PageCaptureDataRepository pageCaptureDataRepository;

	// 테스트 종료 후 "C:/page-capture" 하위의 모든 파일/디렉터리 삭제
	@AfterAll
	static void cleanup() throws IOException {
		Path baseDir = Paths.get("C:/page-capture");
		if (Files.exists(baseDir)) {
			Files.walk(baseDir)
				.sorted(Comparator.reverseOrder())
				.forEach(p -> {
					try {
						Files.deleteIfExists(p);
					} catch (IOException e) {
						System.err.println("삭제 실패: " + p);
					}
				});
		}
	}

	@Test
	@DisplayName("1. 신규 파일 업로드 시 파일 생성 테스트")
	void t001() throws Exception {
		// Given
		FileStorageService fileStorageService = new FileStorageService(path);
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			"image/png",
			"테스트 이미지 데이터".getBytes(StandardCharsets.UTF_8)
		);

		// When: storeFile 메서드가 파일명(UUID + 확장자)만 반환하도록 변경됨
		String fileName = fileStorageService.storeFile(imageFile);

		// Then Assertions
		assertNotNull(fileName, "파일명이 null이어서는 안됩니다.");
		assertTrue(fileName.endsWith(".png"), "파일명이 .png 확장자로 끝나야 합니다.");

		// 실제 파일이 저장되었는지 확인
		Path filePath = Paths.get("C:/page-capture").resolve(fileName);
		assertTrue(Files.exists(filePath), "파일이 실제로 저장되어야 합니다.");

		// 저장된 파일의 내용이 일치하는지 확인
		byte[] storedContent = Files.readAllBytes(filePath);
		assertArrayEquals("테스트 이미지 데이터".getBytes(StandardCharsets.UTF_8),
			storedContent, "파일의 내용이 기대한 값과 일치해야 합니다.");
	}

	@Test
	@DisplayName("2. 기존 파일 덮어쓰기(Override) 테스트")
	void t002() throws Exception {
		// Given
		FileStorageService fileStorageService = new FileStorageService(path);

		// 먼저 신규 업로드로 파일 생성
		MockMultipartFile imageFileOriginal = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			"image/png",
			"원본 파일 데이터".getBytes(StandardCharsets.UTF_8)
		);
		String initialFileName = fileStorageService.storeFile(imageFileOriginal);
		Path filePath = Paths.get("C:/page-capture").resolve(initialFileName);

		assertTrue(Files.exists(filePath), "신규 업로드한 파일이 존재해야 합니다.");
		byte[] originalContent = Files.readAllBytes(filePath);
		assertArrayEquals("원본 파일 데이터".getBytes(StandardCharsets.UTF_8),
			originalContent, "저장된 원본 파일의 내용이 일치해야 합니다.");

		// 기존 엔티티(PageCaptureData)에 원래 파일명이 설정된 상태 생성
		PageCaptureData pageData = PageCaptureData.builder()
			.captureFileName(initialFileName)
			.pageUrl("http://localhost:3000/myPage")
			.build();

		// When: PageCaptureData 객체를 전달하여 기존 파일 덮어쓰기 수행
		MockMultipartFile imageFileUpdated = new MockMultipartFile(
			"imageFile",
			"test-image-updated.png", // 확장자 png
			"image/png",
			"업데이트된 파일 데이터".getBytes(StandardCharsets.UTF_8)
		);
		String updatedFileName = fileStorageService.updateFile(imageFileUpdated, pageData);

		// Then Assertions
		assertNotNull(updatedFileName, "업데이트 후 파일명이 null이어서는 안 됩니다.");
		// 기존 엔티티에 저장된 파일명과 업데이트 후 파일명이 동일해야 함
		assertEquals(initialFileName, updatedFileName, "기존 파일명과 업데이트 후 파일명이 동일해야 합니다.");

		// 실제 파일 내용이 업데이트되었는지 확인
		byte[] updatedContent = Files.readAllBytes(filePath);
		assertArrayEquals("업데이트된 파일 데이터".getBytes(StandardCharsets.UTF_8),
			updatedContent, "파일 내용이 업데이트된 데이터와 일치해야 합니다.");
	}
}
