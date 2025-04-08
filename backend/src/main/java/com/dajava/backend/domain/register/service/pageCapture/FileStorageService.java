package com.dajava.backend.domain.register.service.pageCapture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 캡쳐 이미지를 생성하거나, 기존에 있는 이미지에 덮어쓰는 로직입니다.
 * 기존에 있는 이미지에 덮어쓰는 경우, 동일한 파일명으로 REPLACE 됩니다.
 * @author Metronon
 */
@Service
public class FileStorageService {

	// 파일 저장 경로 (외부 설정에서 주입)
	private final Path fileStorageLocation;

	public FileStorageService(@Value("${image.path}") String storagePath) {
		this.fileStorageLocation = Paths.get(storagePath).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (IOException ex) {
			throw new RuntimeException("디렉토리를 생성하지 못했습니다: " + this.fileStorageLocation, ex);
		}
	}

	/**
	 * 파일을 저장 후 UUID 기반의 파일명(확장자 포함)을 반환
	 */
	public String storeFile(MultipartFile file) {
		String fileName = generateUniqueFileName(file);
		saveFile(fileName, file);
		// UUID와 확장자만으로 구성된 파일명 반환
		return fileName;
	}

	/**
	 * 기존 파일 URL이 있을 경우, 기존 파일명을 그대로 사용하여 파일을 덮어씌웁니다.
	 */
	public String storeFile(MultipartFile file, String existingFileUrl) {
		String fileName = extractFileName(existingFileUrl);
		String fileExtension = getExtension(file.getOriginalFilename());
		if (!fileName.endsWith(fileExtension) && !fileExtension.isEmpty()) {
			fileName += fileExtension;
		}
		saveFile(fileName, file);
		return fileName;
	}

	private void saveFile(String fileName, MultipartFile file) {
		try {
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException ex) {
			throw new RuntimeException("파일 저장에 실패했습니다: " + fileName, ex);
		}
	}

	// MultipartFile 에서 UUID 기반 파일명을 생성합니다.
	private String generateUniqueFileName(MultipartFile file) {
		return UUID.randomUUID().toString() + getExtension(file.getOriginalFilename());
	}

	// 파일명에서 확장자를 추출합니다.
	private String getExtension(String originalFilename) {
		if (originalFilename == null) {
			return "";
		}
		String ext = FilenameUtils.getExtension(originalFilename);
		return (ext != null && !ext.isEmpty()) ? "." + ext : "";
	}

	// 기존 파일 URL 에서 파일명만 추출하는 유틸 메서드
	private String extractFileName(String existingFileUrl) {
		if (existingFileUrl == null || existingFileUrl.isEmpty()) {
			throw new IllegalArgumentException("기존 파일 URL이 유효하지 않습니다.");
		}
		return existingFileUrl.substring(existingFileUrl.lastIndexOf("/") + 1);
	}

	// 컨트롤러에서 사용하기 위해 파일 저장 위치를 노출하는 getter
	public Path getFileStorageLocation() {
		return this.fileStorageLocation;
	}
}



