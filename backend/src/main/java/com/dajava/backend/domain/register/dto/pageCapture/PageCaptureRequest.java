package com.dajava.backend.domain.register.dto.pageCapture;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PageCaptureRequest(

	@Schema(description = "등록된 일련번호", example = "5_team_testSerial", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull String serialNumber,

	@Schema(description = "캡쳐 대상 페이지 URL", example = "http://localhost:3000/myPage", requiredMode = Schema.RequiredMode.REQUIRED)
	String pageUrl,

	@Schema(description = "업로드할 이미지 파일", requiredMode = Schema.RequiredMode.REQUIRED)
	MultipartFile imageFile
) {
}
