package com.dajava.backend.domain.register.dto;

import lombok.Builder;

@Builder
public record SolutionCreateResponse(
	String serialNumber
) {
}
