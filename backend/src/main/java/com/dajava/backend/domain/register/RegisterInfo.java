package com.dajava.backend.domain.register;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterInfo {
	private Long id;
	private String serialNumber;
	private String email;
	private String url;
	private boolean isCompleted;
	private LocalDateTime solutionDate;
	private LocalDateTime solutionCompleteDate;
}

