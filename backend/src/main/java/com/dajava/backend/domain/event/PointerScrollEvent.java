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
public class PointerScrollEvent extends BaseTimeEntity {

	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	int scrollY;

	@Column(nullable = false)
	int browserWidth;

	@ManyToOne
	@JoinColumn(name = "session_data_id")
	private SessionData sessionData;

	public static PointerScrollEvent create(
		int scrollY,
		int browserWidth,
		SessionData sessionData
	) {
		PointerScrollEvent event = PointerScrollEvent.builder()
			.scrollY(scrollY)
			.browserWidth(browserWidth)
			.sessionData(sessionData)
			.build();
		sessionData.addScrollEvent(event); // 양방향 연관관계 설정
		return event;
	}

}
