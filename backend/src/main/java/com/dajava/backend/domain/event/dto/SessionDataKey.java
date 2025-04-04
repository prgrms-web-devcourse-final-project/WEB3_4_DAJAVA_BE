package com.dajava.backend.domain.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 이벤트가 들어올시 SessionDataCache 에 데이터가 존재하는지 확인하기 위한 Key 입니다.
 */
public record SessionDataKey(
	@Schema(description = "사용자의 fingerPrint 로 생성된 식별자", example = "e25f6b52-4a1b-4721-8651-8839f23727cb", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull String sessionId,

	@Schema(description = "세션의 페이지 URL 정보", example = "localhost:3000/myPage", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull String pageUrl,

	@Schema(description = "행동 솔루션 신청시 생성된 UUID 식별자", example = "a07cb1fc-e5db-4578-89a6-34d7a31f9389", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull String memberSerialNumber
) {
}

