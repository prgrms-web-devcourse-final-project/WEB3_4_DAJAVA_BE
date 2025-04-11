package com.dajava.backend.domain.log.dto.base;

import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEventRequest {
	@Schema(description = "랜덤으로 생성된 이벤트 식별자", example = "test-event-id", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull private String eventId;

	@Schema(description = "로그 데이터의 생성 시각", example = "1711963200000", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull private Long timestamp;

	@Schema(description = "현재 브라우저 창의 가로 길이", example = "1024", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull private Integer browserWidth;

	@Schema(description = "전체 페이지의 세로 길이", example = "1500", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull private Integer scrollHeight;

	@Schema(description = "현재 브라우저 창의 세로 길이", example = "500", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull private Integer viewportHeight;

	@NotNull
	@JsonUnwrapped // 평탄화 어노테이션
	private SessionIdentifier sessionIdentifier;
}
