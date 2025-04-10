package com.dajava.backend.domain.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
/**
 * Click(Touch) 관련 요청 DTO 입니다.
 */
public class ClickEventRequest extends BaseEventRequest {

	@Schema(description = "클릭시 대상의 태그 정보", example = "div", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull
	private String element;

	public ClickEventRequest(String eventId, String sessionId, String pageUrl, String memberSerialNumber,
		Long timestamp, Integer browserWidth, Integer scrollHeight, Integer viewportHeight,
		String element) {
		super(eventId, sessionId, pageUrl, memberSerialNumber, timestamp, browserWidth, scrollHeight, viewportHeight);
		this.element = element;
	}

	public String getElement() {
		return element;
	}
}
