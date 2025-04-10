package com.dajava.backend.domain.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEventRequest {
	@Schema(description = "랜덤으로 생성된 이벤트 식별자", example = "e25f6b52-4a1b-4721-8651-8839f23727cb", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private String eventId;

	@Schema(description = "사용자의 fingerPrint 로 생성된 식별자", example = "e25f6b52-4a1b-4721-8651-8839f23727cb", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private String sessionId;

	@Schema(description = "세션의 페이지 URL 정보", example = "localhost:3000/myPage", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private String pageUrl;

	@Schema(description = "행동 솔루션 신청시 생성된 UUID 식별자", example = "5_team_testSerial", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private String memberSerialNumber;

	@Schema(description = "로그 데이터의 생성 시각", example = "1711963200000", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Long timestamp;

	@Schema(description = "현재 브라우저 창의 가로 길이", example = "1280", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer browserWidth;

	@Schema(description = "전체 페이지의 세로 길이", example = "1500", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer scrollHeight;

	@Schema(description = "현재 브라우저 창의 세로 길이", example = "500", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer viewportHeight;
}
