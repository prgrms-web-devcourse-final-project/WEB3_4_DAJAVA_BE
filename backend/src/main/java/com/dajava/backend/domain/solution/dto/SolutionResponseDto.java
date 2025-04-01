package com.dajava.backend.domain.solution.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;


public record SolutionResponseDto (
	@NotBlank(message = "등록 serial 번호는 비어 있을 수 없습니다.")
	String registerSerialNumber,

	@Lob
	@NotBlank(message = "솔루션 내용은 비어 있을 수 없습니다.")
	String text

) {}