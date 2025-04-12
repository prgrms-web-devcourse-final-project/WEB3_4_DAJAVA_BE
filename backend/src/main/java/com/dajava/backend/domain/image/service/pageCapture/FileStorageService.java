package com.dajava.backend.domain.image.service.pageCapture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.dajava.backend.domain.image.ImageDimensions;
import com.dajava.backend.domain.image.exception.ImageException;
import com.dajava.backend.domain.register.entity.PageCaptureData;
import com.dajava.backend.global.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;

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
	 * 전달받은 PageCaptureData 객체를 사용하여 파일을 저장합니다.
	 * 기존 파일명이 있는 경우 해당 파일명으로 덮어쓰고, 없으면 새로 생성한 후 엔티티에 반영합니다.
	 */
	public String updateFile(MultipartFile file, PageCaptureData pageData) {
		String fileExtension = getExtension(file.getOriginalFilename());
		String fileName = pageData.getCaptureFileName();

		if (fileName == null || fileName.isEmpty()) {
			fileName = generateUniqueFileName(file);
		} else if (!fileName.endsWith(fileExtension) && !fileExtension.isEmpty()) {
			fileName += fileExtension;
		}

		pageData.updateCaptureFileName(fileName);
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

	/**
	 * 주어진 파일 이름과 HttpServletRequest 를 사용하여 파일 시스템에 저장된 이미지를
	 * Resource 형태로 로드합니다.
	 *
	 * @param fileName 이미지 파일 이름 (예: UUID 기반의 파일명 + 확장자)
	 * @return Resource 형태의 이미지 파일
	 */
	public Resource getImage(String fileName) {
		try {
			// 파일 저장 경로에서 파일명을 사용해 절대 파일 경로 계산
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();

			// 보안 체크: 계산된 경로가 fileStorageLocation 하위에 있는지 확인
			if (!filePath.startsWith(this.fileStorageLocation)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 파일 경로입니다.");
			}

			Resource resource = new UrlResource(filePath.toUri());

			if (!resource.exists() || !resource.isReadable()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.");
			}

			return resource;
		} catch (MalformedURLException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 파일 경로입니다.", ex);
		}
	}

	/**
	 * 주어진 파일 이름으로 이미지의 높이와 너비를 추출하는 메서드
	 * @param fileName 이미지 파일 이름 (예: UUID 기반의 파일명 + 확장자)
	 * @return 이미지의 높이와 너비를 담은 DTO 객체
	 */
	public ImageDimensions getImageDimensions(String fileName) {
		try {
			// 기존 getImage() 메서드 호출
			Resource imageResource = getImage(fileName);

			// 이미지 스트림을 BufferedImage 로 변환
			BufferedImage image = ImageIO.read(imageResource.getInputStream());
			if (image == null) {
				throw new ImageException(ErrorCode.INVALID_IMAGE_FILE);
			}

			// 높이와 너비 추출
			int width = image.getWidth();
			int height = image.getHeight();

			return new ImageDimensions(width, height);
		} catch (IOException e) {
			throw new ImageException(ErrorCode.IMAGE_IO_ERROR);
		}
	}

	public String determineContentType(Resource resource, HttpServletRequest request) {
		try {
			String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			return (contentType != null && !contentType.isEmpty())
				? contentType
				: "application/octet-stream";
		} catch (Exception ex) {
			// MIME 타입 결정 실패 시 기본 값 반환
			return "application/octet-stream";
		}
	}
}



