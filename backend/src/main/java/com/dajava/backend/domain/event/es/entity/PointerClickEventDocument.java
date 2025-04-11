package com.dajava.backend.domain.event.es.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.global.exception.ErrorCode;
import com.dajava.backend.global.utils.TimeUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * api로 들어온 clickEvent를 저장하는 ES 인덱스 입니다.
 * @author NohDongHui
 */
@Document(indexName = "pointer-click-events")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PointerClickEventDocument {

	@Id
	private String id;

	private String sessionId;
	private String pageUrl;
	private String memberSerialNumber;

	private Long timestamp;

	private Integer browserWidth;
	private Integer clientX;
	private Integer clientY;
	private Integer scrollY;
	private Integer scrollHeight;
	private Integer viewportHeight;
	private String element;

	private Boolean isOutlier;

	public void markAsOutlier() {
		if (this.isOutlier) {
			throw new PointerEventException(ErrorCode.ALREADY_OUTLIER_DOCUMENT);
		}
		this.isOutlier = true;
	}

	public LocalDateTime getTimestamp() {
		return TimeUtils.toLocalDateTime(this.timestamp);
	}

	public boolean isValid() {
		return sessionId != null &&
			pageUrl != null &&
			timestamp != null &&
			clientX != null &&
			clientY != null &&
			element != null &&
			scrollHeight != null &&
			viewportHeight != null &&
			browserWidth != null &&
			id != null &&
			memberSerialNumber != null &&
			scrollY != null;
	}
}
