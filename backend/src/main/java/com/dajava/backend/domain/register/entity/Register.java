package com.dajava.backend.domain.register.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.dajava.backend.domain.register.dto.RegisterCreateRequest;
import com.dajava.backend.domain.solution.SolutionEntity;
import com.dajava.backend.global.common.BaseTimeEntity;
import com.dajava.backend.global.utils.PasswordUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Register extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String serialNumber;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String url;

	@Column(nullable = false)
	private LocalDateTime startDate;

	@Column(nullable = false)
	private LocalDateTime endDate;

	@Column(nullable = false)
	private int duration;

	@Column(nullable = false)
	private boolean isServiceExpired;

	@Column(nullable = false)
	private boolean isSolutionComplete;

	@OneToOne(mappedBy = "register", cascade = CascadeType.ALL, orphanRemoval = true)
	private SolutionEntity solution;

	public static Register create(
		final RegisterCreateRequest request,
		final int duration
	) {
		return Register.builder()
			.serialNumber(createSerialNumber())
			.email(request.email())
			.password(PasswordUtils.hashPassword(request.password()))
			.url(request.url())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.duration(duration)
			.isServiceExpired(false)
			.isSolutionComplete(false)
			.build();
	}

	private static String createSerialNumber() {
		return UUID.randomUUID().toString();
	}

	@Override
	public String toString() {
		return "Solution{"
			+ "serialNumber='" + serialNumber + '\''
			+ ", email='" + email + '\''
			+ ", password='" + password + '\''
			+ ", url='" + url + '\''
			+ ", startDate=" + startDate
			+ ", endDate=" + endDate
			+ ", duration=" + duration
			+ ", isServiceExpired=" + isServiceExpired
			+ ", isSolutionComplete=" + isSolutionComplete
			+ '}';
	}
}
