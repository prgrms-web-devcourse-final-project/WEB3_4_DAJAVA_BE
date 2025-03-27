package com.dajava.backend.domain.event;

import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PointerClickEvent extends BaseTimeEntity {

	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	Integer clientX;

	@Column(nullable = false)
	Integer clientY;

	@Column(nullable = false)
	String pageUrl;

	@Column(nullable = false)
	Integer browserWidth;

	@Column(nullable = false)
	String sessionId;

	@Column(nullable = false)
	String memberSerialNumber;

	@ManyToOne
	@JoinColumn(name = "session_data_id")
	private SessionData sessionData;

	@Column(nullable = false)
	boolean isOutlier;

	public static PointerClickEvent create(
		int clientX,
		int clientY,
		String pageUrl,
		int browserWidth,
		String sessionId,
		String memberSerialNumber,
		SessionData sessionData
	) {
		PointerClickEvent event = PointerClickEvent.builder()
			.clientX(clientX)
			.clientY(clientY)
			.pageUrl(pageUrl)
			.browserWidth(browserWidth)
			.sessionId(sessionId)
			.memberSerialNumber(memberSerialNumber)
			.isOutlier(false)
			.sessionData(sessionData)
			.build();
		sessionData.addClickEvent(event); // 양방향 연관관계 설정
		return event;
	}

	public void setOutlier() {
		if (this.isOutlier) {
			throw new IllegalStateException("이미 이상치 여부 값이 참입니다.");
		}
		this.isOutlier = true;
	}
}

