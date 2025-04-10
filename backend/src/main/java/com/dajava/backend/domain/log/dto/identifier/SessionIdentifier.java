package com.dajava.backend.domain.log.dto.identifier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SessionIdentifier(
	@Schema(description = "session 고유 ID", example = "e25f6b52-4a1b-4721-8651-8839f23727cb", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull String sessionId,

	@Schema(description = "솔루션 URL 정보", example = "localhost:3000/myPage", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull String pageUrl,

	@Schema(description = "행동 솔루션 신청시 생성된 UUID 식별자", example = "5_team_testSerial", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull String memberSerialNumber
) {
}
