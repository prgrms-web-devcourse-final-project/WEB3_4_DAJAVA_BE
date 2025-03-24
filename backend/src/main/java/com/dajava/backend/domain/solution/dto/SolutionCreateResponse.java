package com.dajava.backend.domain.solution.dto;

import lombok.Builder;

@Builder
public record SolutionCreateResponse(
	String serialNumber
) {
}
