package com.dajava.backend.domain.solution.dto;

import jakarta.validation.constraints.NotBlank;

public record SolutionResponseDto (
	@NotBlank String registerSerialNumber,
	@NotBlank  String text
) {}
