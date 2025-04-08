package com.dajava.backend.domain.register.service.pageCapture;

import java.io.IOException;
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

	private final Path fileStorageLocation;

	public FileStorageService(@Value("${image.path}") String storagePath) {
		this.fileStorageLocation = Paths.get(storagePath).toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (IOException ex) {
			throw new RuntimeException("디렉토리를 생성하지 못했습니다.", ex);
		}
	}

	/**
	 * 기존 파일 URL 이 없는 경우, 새로운 UUID 기반 파일명 생성 로직
	 */
	public String storeFile(String pageUrl, MultipartFile file) {
		// 파일의 확장자 추출
		String originalExtension = FilenameUtils.getExtension(file.getOriginalFilename());
		String extension = (originalExtension != null && !originalExtension.isEmpty())
			? "." + originalExtension : "";
		// 새 UUID 기반 파일명 생성
		String fileName = UUID.randomUUID().toString() + extension;

		return saveFile(fileName, file);
	}

	/**
	 * 기존 파일 URL이 있을 경우, 기존 파일명을 그대로 사용하여 덮어쓰는 로직
	 */
	public String storeFile(String pageUrl, MultipartFile file, String existingFileUrl) {
		// 파일명 추출
		String fileName = existingFileUrl.substring(existingFileUrl.lastIndexOf("/") + 1);
		// 파일의 확장자 추출 (기존 파일명에 이미 확장자가 없는 경우에는 추가)
		String originalExtension = FilenameUtils.getExtension(file.getOriginalFilename());
		String extension = (originalExtension != null && !originalExtension.isEmpty())
			? "." + originalExtension : "";
		if (!fileName.endsWith(extension)) {
			fileName = fileName + extension;
		}

		return saveFile(fileName, file);
	}

	/**
	 * UUID 로 구성된 파일명 (확장자 포함) 과 파일로 저장 후, 경로를 반환하는 로직
	 *
	 * @param fileName UUID 로 생성 혹은 가져온 기존 이름입니다.
	 * @param file 멀티파트 이미지 파일입니다.
	 * @return String pageCapturePath 로 저장될 경로입니다.
	 */
	private String saveFile(String fileName, MultipartFile file) {
		try {
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			// 기존 파일이 있더라도 덮어씁니다.
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return "/page-capture/" + fileName;
		} catch (IOException ex) {
			throw new RuntimeException("파일 저장에 실패했습니다.", ex);
		}
	}
}


