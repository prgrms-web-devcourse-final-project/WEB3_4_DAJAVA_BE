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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
public class FileStorageServiceTest {

	// 테스트 종료 후 "C:/page-capture" 하위 모든 파일/디렉터리 삭제
	@AfterAll
	static void cleanup() throws IOException {
		Path baseDir = Paths.get("C:/page-capture");
		if (Files.exists(baseDir)) {
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
	@DisplayName("1. 신규 파일 업로드 시 파일 생성 테스트")
	void t001() throws Exception {
		// Given
		FileStorageService fileStorageService = new FileStorageService();
		String pageUrl = "http://localhost:3000/myPage";
		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			"image/png",
			"테스트 이미지 데이터".getBytes(StandardCharsets.UTF_8)
		);

		// when
		String fileUrl = fileStorageService.storeFile(imageFile);

		// then
		assertNotNull(fileUrl);
		assertTrue(fileUrl.startsWith("/page-capture/"));
		assertTrue(fileUrl.endsWith(".png"));

		// 실제 파일이 저장되었는지 확인
		String storedFileName = fileUrl.substring("/page-capture/".length());
		Path filePath = Paths.get("C:/page-capture").resolve(storedFileName);
		assertTrue(Files.exists(filePath));

		// 저장된 파일의 내용이 일치하는지 확인
		byte[] storedContent = Files.readAllBytes(filePath);
		assertArrayEquals("테스트 이미지 데이터".getBytes(StandardCharsets.UTF_8), storedContent);
	}

	@Test
	@DisplayName("2. 기존 파일 덮어쓰기(Override) 테스트")
	void t002() throws Exception {
		// given
		FileStorageService fileStorageService = new FileStorageService();
		String pageUrl = "http://localhost:3000/myPage";

		// 먼저 신규 업로드로 파일을 생성
		MockMultipartFile imageFileOriginal = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			"image/png",
			"원본 파일 데이터".getBytes(StandardCharsets.UTF_8)
		);
		String originalFileUrl = fileStorageService.storeFile(imageFileOriginal);
		String originalFileName = originalFileUrl.substring("/page-capture/".length());
		Path filePath = Paths.get("C:/page-capture").resolve(originalFileName);

		assertTrue(Files.exists(filePath));
		byte[] originalContent = Files.readAllBytes(filePath);
		assertArrayEquals("원본 파일 데이터".getBytes(StandardCharsets.UTF_8), originalContent);

		// when: 기존 파일 URL 을 이용하여 새로운 파일 업로드(덮어쓰기) 수행
		MockMultipartFile imageFileUpdated = new MockMultipartFile(
			"imageFile",
			"test-image-updated.png", // 확장자 png
			"image/png",
			"업데이트된 파일 데이터".getBytes(StandardCharsets.UTF_8)
		);
		String updatedFileUrl = fileStorageService.storeFile(imageFileUpdated, originalFileUrl);

		// then
		assertNotNull(updatedFileUrl);
		String updatedFileName = updatedFileUrl.substring("/page-capture/".length());
		// 기존 파일명과 새로운 파일명이 동일해야함
		assertEquals(originalFileName, updatedFileName);

		// 실제 파일 내용이 변경되었는지 확인
		byte[] updatedContent = Files.readAllBytes(filePath);
		assertArrayEquals("업데이트된 파일 데이터".getBytes(StandardCharsets.UTF_8), updatedContent);
	}
}
