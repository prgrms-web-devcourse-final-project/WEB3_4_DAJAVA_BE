package com.dajava.backend.domain.solution.service;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.domain.solution.dto.SolutionInfoResponse;
import com.dajava.backend.domain.solution.dto.SolutionResponseDto;

import reactor.core.publisher.Mono;

public interface SolutionService {
	/**
	 * Gemini API 답변 요청 메서드
	 * @param refineData // 요청할 데이터
	 * @param serialNumber // 신청자에게 제공된 시리얼 넘버
	 * @return result // 처리된 응답 데이터
	 */
	Mono<SolutionResponseDto> getAISolution(String refineData, String serialNumber);

	/**
	 * 시리얼 넘버와 비밀번호로 솔루션 정보를 조회하는 메서드
	 * @param serialNumber // 신청자에게 제공된 시리얼 넘버
	 * @param password // 신청자가 작성한 비밀번호
	 * @return SolutionInfoResponse // 솔루션 정보 응답 객체
	 * @throws RegisterException 시리얼 넘버를 찾을 수 없거나, 비밀번호가 일치하지 않거나, 솔루션 정보가 없을 경우 발생
	 */
	SolutionInfoResponse getSolutionInfo(String serialNumber, String password);

	SolutionData getSolutionData(String serialNumber);
}
