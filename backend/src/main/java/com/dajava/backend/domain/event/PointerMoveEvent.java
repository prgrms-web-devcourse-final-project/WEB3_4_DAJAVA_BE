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
public class PointerMoveEvent extends BaseTimeEntity {

	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	int clientX;

	@Column(nullable = false)
	int clientY;

	@Column(nullable = false)
	int browserWidth;

	@ManyToOne
	@JoinColumn(name = "session_data_id")
	private SessionData sessionData;

	public static PointerMoveEvent create(
		int clientX,
		int clientY,
		int browserWidth,
		SessionData sessionData
	) {
		PointerMoveEvent event = PointerMoveEvent.builder()
			.clientX(clientX)
			.clientY(clientY)
			.browserWidth(browserWidth)
			.sessionData(sessionData)
			.build();
		sessionData.addMoveEvent(event); // 양방향 연관관계 설정
		return event;
	}
}
