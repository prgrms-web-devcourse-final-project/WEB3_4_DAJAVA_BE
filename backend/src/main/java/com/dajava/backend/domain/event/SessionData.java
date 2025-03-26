package com.dajava.backend.domain.event;

import java.util.ArrayList;
import java.util.List;

import com.dajava.backend.global.common.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class SessionData extends BaseTimeEntity {

	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	boolean isOutlier;

	@Column(nullable = false)
	boolean isMissingValue;

	@Column(nullable = false)
	String pageUrl;

	@Column(nullable = false)
	String sessionId;

	@Column(nullable = false)
	String memberSerialNumber;

	// 해당 세션이 종료되었는지 확인하는 boolean flag
	@Column(nullable = false)
	boolean isSessionEnded;

	// 이상치, 결측치 검증을 했는지 확인하는 boolean flag
	@Column(nullable = false)
	boolean isVerified;

	@OneToMany(mappedBy = "sessionData", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PointerClickEvent> pointerClickEvents = new ArrayList<>();

	@OneToMany(mappedBy = "sessionData", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PointerMoveEvent> pointerMoveEvents = new ArrayList<>();

	@OneToMany(mappedBy = "sessionData", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PointerScrollEvent> pointerScrollEvents = new ArrayList<>();

	public void addClickEvent(PointerClickEvent event) {
		pointerClickEvents.add(event);
	}

	public void addMoveEvent(PointerMoveEvent event) {
		pointerMoveEvents.add(event);
	}

	public void addScrollEvent(PointerScrollEvent event) {
		pointerScrollEvents.add(event);
	}

	public static SessionData create(String pageUrl, String sessionId, String memberSerialNumber) {
		SessionData session = new SessionData();
		session.pageUrl = pageUrl;
		session.sessionId = sessionId;
		session.memberSerialNumber = memberSerialNumber;

		session.isOutlier = false;
		session.isMissingValue = false;
		session.isSessionEnded = false;
		session.isVerified = false;

		// 리스트는 이미 초기화되어 있음 (@Builder.Default 또는 생성자 내부에서)

		return session;
	}
}

