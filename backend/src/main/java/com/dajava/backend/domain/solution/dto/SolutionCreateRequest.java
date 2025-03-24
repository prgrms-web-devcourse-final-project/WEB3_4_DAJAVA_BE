package com.dajava.backend.domain.solution.dto;

import java.time.LocalDateTime;

import lombok.NonNull;

public record SolutionCreateRequest(
	@NonNull
	String email,

	@NonNull
	String password,

	@NonNull
	String url,

	@NonNull
	LocalDateTime startDate,

	@NonNull
	LocalDateTime endDate
) {

	@Override
	public String toString() {
		return "솔루션 신청 정보: "
			+ "[email=" + email + ", "
			+ "password=" + password + ", "
			+ "url=" + url + ", "
			+ "startDate=" + startDate + ", "
			+ "endDate=" + endDate + "]";
	}
}
