package com.dajava.backend.domain.register.converter;

import com.dajava.backend.domain.register.dto.SolutionCreateResponse;
import com.dajava.backend.domain.register.entity.Solution;

/**
 * RegisterConstant
 * Solution Register 도메인 관련 변환 역할을 담당하는 클래스
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
public class RegisterConverter {

	public static SolutionCreateResponse toSolutionCreateResponse(final Solution solution) {
		return SolutionCreateResponse.builder()
			.serialNumber(solution.getSerialNumber())
			.build();
	}
}
