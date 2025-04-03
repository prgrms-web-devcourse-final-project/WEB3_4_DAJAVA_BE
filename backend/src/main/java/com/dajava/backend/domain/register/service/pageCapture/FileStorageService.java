package com.dajava.backend.domain.register.service.pageCapture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

	private static final String STORAGE_PATH = "C:/page-capture";

	private final Path fileStorageLocation;

	// 해당 경로에 디렉토리가 없다면 생성
	public FileStorageService() {
		this.fileStorageLocation = Paths.get(STORAGE_PATH).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (IOException ex) {
			throw new RuntimeException("디렉토리를 생성하지 못했습니다.", ex);
		}
	}

	/**
	 *
	 * @param file 전송받는 멀티파트 파일
	 * @return String UUID 형식으로 로컬에 저장된 이미지의 경로값을 반환
	 */
	public String storeFile(MultipartFile file) {
		// 파일의 확장자 추출
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (extension != null && !extension.isEmpty()) {
			extension = "." + extension;
		} else {
			extension = "";
		}

		// UUID + 확장자명으로 새로운 고유 파일명 생성
		String fileName = UUID.randomUUID().toString() + extension;

		try {
			// 정의한 파일 경로로 멀티파트 파일 복사
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return "/page-capture/" + fileName;
		} catch (IOException ex) {
			throw new RuntimeException("파일 저장에 실패했습니다: ", ex);
		}
	}
}
