package com.dajava.backend.domain.solution.dto;

import jakarta.validation.constraints.NotNull;

public record SolutionInfoResponse(
	@NotNull String text
	) {
}
