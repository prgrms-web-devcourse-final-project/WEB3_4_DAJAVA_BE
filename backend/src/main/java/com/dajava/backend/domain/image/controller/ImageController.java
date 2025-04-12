package com.dajava.backend.domain.image.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.image.service.pageCapture.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "ImageController", description = "이미지 파일 반환 컨트롤러")
@RequestMapping("/v1/images")
public class ImageController {

	private final FileStorageService fileStorageService;

	public ImageController(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	/**
	 * 이미지 파일 조회 엔드포인트.
	 * URL 경로의 파일명으로 파일을 로드합니다
	 *
	 * 예) GET /v1/images/{fileName}
	 *
	 * @param fileName 조회할 파일의 이름 (UUID 기반 파일명 + 확장자)
	 * @param request  HttpServletRequest (MIME 타입 결정용)
	 * @return Resource 형태의 이미지 파일
	 */
	@Operation(
		summary = "이미지 캡쳐 파일 로드",
		description = "URL 경로의 파일명으로 파일을 로드합니다")
	@GetMapping("/{fileName:.+}")
	public ResponseEntity<Resource> getImage(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource = fileStorageService.getImage(fileName);

		String contentType = fileStorageService.determineContentType(resource, request);

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.body(resource);
	}
}
