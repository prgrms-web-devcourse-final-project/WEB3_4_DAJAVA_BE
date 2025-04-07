package com.dajava.backend.domain.register.dto.pageCapture;

import io.swagger.v3.oas.annotations.media.Schema;

public record PageCaptureResponse(

	@Schema(description = "요청의 성공 여부", example = "true")
	boolean success,

	@Schema(description = "결과 메시지", example = "페이지 캡쳐 데이터가 성공적으로 업데이트되었습니다.")
	String message,

	@Schema(description = "저장된 캡쳐 이미지의 URL", example = "/page-capture/d4fcb5a1-5cb6-4a95-902c-d2baacf6e9c8")
	String pageCaptureUrl
) {
}
