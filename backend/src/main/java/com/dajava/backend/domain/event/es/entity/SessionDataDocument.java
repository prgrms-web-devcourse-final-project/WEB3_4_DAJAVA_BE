package com.dajava.backend.domain.event.es.entity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.global.exception.ErrorCode;
import com.dajava.backend.global.utils.TimeUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * sessionData를 저장하는 ES 인덱스 입니다.
 * @author NohDongHui
 */
@Document(indexName = "session-data")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDataDocument {

	@Id
	private String id; // sessionId + pageurl + memberSerialNumber 사용

	private String sessionId;

	private String memberSerialNumber;

	private String pageUrl;

	private boolean isOutlier;

	private boolean isMissingValue;

	private Long timestamp;

	private boolean isSessionEnded;

	private boolean isVerified;

	public void endSession() {
		if (this.isSessionEnded) {
			throw new PointerEventException(ErrorCode.ALREADY_ENDED_SESSION);
		}
		this.isSessionEnded = true;
	}

	public void markAsVerified() {
		if (this.isVerified) {
			throw new PointerEventException(ErrorCode.ALREADY_VERIFIED_SESSION);
		}
		this.isVerified = true;
	}

	public static SessionDataDocument create(String sessionId, String memberSerialNumber, String pageUrl, Long timestamp) {
		String id = sessionId + "_" + pageUrl + "_" + memberSerialNumber;

		return SessionDataDocument.builder()
			.id(id)
			.sessionId(sessionId)
			.memberSerialNumber(memberSerialNumber)
			.pageUrl(pageUrl)
			.timestamp(timestamp)
			.isOutlier(false)
			.isMissingValue(false)
			.isSessionEnded(false)
			.isVerified(false)
			.build();
	}

	public LocalDateTime getTimestamp() {
		return TimeUtils.toLocalDateTime(this.timestamp);
	}
}