package com.dajava.backend.domain.register;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterInfo {
	private Long id;
	private String serialNumber;
	private String email;
	private String url;
	private LocalDateTime solutionDate;
	private LocalDateTime solutionStartDate;
	private LocalDateTime solutionEndDate;
	private boolean isCompleted;
	private String eventState;
}

