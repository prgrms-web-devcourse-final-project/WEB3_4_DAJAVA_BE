package com.dajava.backend.domain.log.dto.identifier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이벤트가 들어올 시 SessionDataCache 에 데이터가 존재하는지 확인하기 위한 Key 입니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SessionIdentifier {
	@Schema(description = "session 고유 ID", example = "test-session-id", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private String sessionId;

	@Schema(description = "솔루션 URL 정보", example = "localhost:3000", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private String pageUrl;

	@Schema(description = "행동 솔루션 신청 시 생성된 UUID 식별자", example = "5_team_testSerial", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private String memberSerialNumber;
}
