package com.dajava.backend.domain.event.es.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.dajava.backend.domain.event.exception.PointerEventException;
import com.dajava.backend.global.exception.ErrorCode;

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
}