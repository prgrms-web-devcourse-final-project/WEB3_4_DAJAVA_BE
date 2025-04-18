package com.dajava.backend.domain.event.entity;

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
public class PointerMoveEvent extends BaseTimeEntity implements PointerEvent {

	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	Integer clientX;

	@Column(nullable = false)
	Integer clientY;

	@Column(nullable = false)
	int scrollY;

	@Column(nullable = false)
	int scrollHeight;

	@Column(nullable = false)
	int viewportHeight;

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

	public static PointerMoveEvent create(
		int clientX,
		int clientY,
		int scrollY,
		int scrollHeight,
		int viewportHeight,
		String pageUrl,
		int browserWidth,
		String sessionId,
		String memberSerialNumber,
		SessionData sessionData
	) {
		PointerMoveEvent event = PointerMoveEvent.builder()
			.clientX(clientX)
			.clientY(clientY)
			.scrollY(scrollY)
			.scrollHeight(scrollHeight)
			.viewportHeight(viewportHeight)
			.pageUrl(pageUrl)
			.browserWidth(browserWidth)
			.sessionId(sessionId)
			.memberSerialNumber(memberSerialNumber)
			.sessionData(sessionData)
			.build();
		sessionData.addMoveEvent(event); // 양방향 연관관계 설정
		return event;
	}

}
