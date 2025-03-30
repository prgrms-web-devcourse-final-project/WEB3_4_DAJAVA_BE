package com.dajava.backend.domain.register.converter;

import com.dajava.backend.domain.register.EventState;
import com.dajava.backend.domain.register.RegisterInfo;
import com.dajava.backend.domain.register.dto.register.RegisterCreateResponse;
import com.dajava.backend.domain.register.entity.Register;

/**
 * RegisterConstant
 * Solution Register 도메인 관련 변환 역할을 담당하는 클래스
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
public class RegisterConverter {

	public static RegisterInfo toRegisterInfo(Register solution) {
		return RegisterInfo.builder()
			.id(solution.getId())
			.serialNumber(solution.getSerialNumber())
			.email(solution.getEmail())
			.url(solution.getUrl())
			.isCompleted(solution.isSolutionComplete())
			.solutionDate(solution.getCreateDate())
			.solutionStartDate(solution.getStartDate())
			.solutionEndDate(solution.getEndDate())
			.eventState(EventState.IN_PROGRESS.getState()) // 아직 엔티티에 추가하지 않았으므로 기본 값으로 넣어놓음
			.build();
	}

	public static RegisterCreateResponse toRegisterCreateResponse(final Register solution) {
		return RegisterCreateResponse.builder()
			.serialNumber(solution.getSerialNumber())
			.build();
	}
}
