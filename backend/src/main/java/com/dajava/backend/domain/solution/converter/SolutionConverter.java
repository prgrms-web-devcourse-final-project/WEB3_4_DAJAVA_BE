package com.dajava.backend.domain.solution.converter;

import com.dajava.backend.domain.solution.dto.SolutionCreateResponse;
import com.dajava.backend.domain.solution.entity.Solution;

public class SolutionConverter {

	public static SolutionCreateResponse toSolutionCreateResponse(final Solution solution) {
		return SolutionCreateResponse.builder()
			.serialNumber(solution.getSerialNumber())
			.build();
	}
}
