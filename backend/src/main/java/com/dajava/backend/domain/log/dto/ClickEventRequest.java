package com.dajava.backend.domain.log.dto;

import com.dajava.backend.domain.log.dto.base.BaseEventRequest;
import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClickEventRequest extends BaseEventRequest {
	@Schema(description = "이동시 X 좌표값", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer clientX;

	@Schema(description = "이동시 Y 좌표값", example = "500", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private Integer clientY;

	@NotNull
	private Integer scrollY;

	@NotNull
	private String tag;

	@Builder
	public ClickEventRequest(
		String eventId,
		Long timestamp,
		Integer browserWidth,
		Integer scrollHeight,
		Integer viewportHeight,
		SessionIdentifier sessionIdentifier,
		Integer clientX,
		Integer clientY,
		Integer scrollY,
		String tag
	) {
		super(eventId, timestamp, browserWidth, scrollHeight, viewportHeight, sessionIdentifier);
		this.clientX = clientX;
		this.clientY = clientY;
		this.scrollY = scrollY;
		this.tag = tag;
	}

}
