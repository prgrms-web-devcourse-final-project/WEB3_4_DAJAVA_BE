package com.dajava.backend.domain.register.service.pageCapture;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
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
	@DisplayName("FileStorageService 파일 저장 테스트")
	void t001() throws Exception {
		// given
		FileStorageService fileStorageService = new FileStorageService();

		MockMultipartFile imageFile = new MockMultipartFile(
			"imageFile",
			"test-image.png",
			"image/png",
			"테스트 이미지 데이터".getBytes()
		);

		// when
		String fileUrl = fileStorageService.storeFile(imageFile);

		// then
		assertNotNull(fileUrl);
		assertTrue(fileUrl.startsWith("/page-capture/"));
		assertTrue(fileUrl.endsWith(".png"));

		// 실제 파일이 존재하는지 확인
		Path filePath = Paths.get("C:/page-capture").resolve(fileUrl.substring("/page-capture/".length()));
		assertTrue(Files.exists(filePath));
	}
}
