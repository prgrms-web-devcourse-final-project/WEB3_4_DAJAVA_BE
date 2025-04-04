package com.dajava.backend.domain.register.entity;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.domain.solution.entity.Solution;
import com.dajava.backend.global.common.BaseTimeEntity;
import com.dajava.backend.global.utils.PasswordUtils;
import com.dajava.backend.global.utils.TimeUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private Solution solution;

	public static Register create(
		final RegisterCreateRequest request
	) {
		return Register.builder()
			.serialNumber(createSerialNumber())
			.email(request.email())
			.password(PasswordUtils.hashPassword(request.password()))
			.url(request.url())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.duration(TimeUtils.getDuration(request.startDate(), request.endDate()))
			.isServiceExpired(false)
			.isSolutionComplete(false)
			.build();
	}

	public void setSolutionComplete(boolean solutionComplete) {
		isSolutionComplete = solutionComplete;
	}

	private static String createSerialNumber() {
		return UUID.randomUUID().toString();
	}

	public void updateEndDate(LocalDateTime newEndDate) {
		// 변경하려는 시간이 현재 지정 시간과 일정 기간 이상 차이가 나는 경우에는 예외를 반환
		if (Math.abs(ChronoUnit.DAYS.between(endDate, newEndDate)) > 7) {
			throw new RegisterException(MODIFY_DATE_EXCEEDED);
		}

		// endDateTime & duration 갱신
		this.duration += TimeUtils.getDuration(endDate, newEndDate);
		this.endDate = newEndDate;
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
