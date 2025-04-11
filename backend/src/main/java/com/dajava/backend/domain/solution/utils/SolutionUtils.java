package com.dajava.backend.domain.solution.utils;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.util.List;

import com.dajava.backend.domain.solution.dto.SolutionRequest;
import com.dajava.backend.domain.solution.exception.SolutionException;

public final class SolutionUtils {
	/**
	 * 사용자 세션 데이터를 기반으로 UI/UX 개선점을 제안하는 프롬프트를 생성합니다.
	 * @param eventData 사용자 로그 데이터만 담은 객체
	 * @return 생성된 프롬프트 문자열
	 */
	public static String refinePrompt(List<SolutionRequest.EventDataDto> eventData) {
		return String.format("다음 사용자 세션 데이터를 분석하여 UI/UX 개선점을 제안해주세요. 브라우저 width는 1024px이고 브라우저 환경은 Chrome이야 글자는 400자 이내로 해줘: %s", eventData);
	}
	/**
	 * 프롬프트를 바탕으로 AI에게 전달할 데이터 형식을 생성합니다.
	 * @param prompt UI/UX 개선을 위한 프롬프트
	 * @return AI 모델에게 전달할 데이터 문자열
	 */
	public static String buildRefineData(String prompt) {
		return String.format("{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}", prompt);
	}
	/**
	 * SolutionData 객체에서 serialNumber를 추출합니다.
	 * @param solutionRequest solutionRequestDto 객체
	 * @return solutionRequestDto.serialNumber() serialNumber
	 * @throws IllegalArgumentException solutionRequestDto가 null인 경우
	 */
	public static String extractsSerialNumber(SolutionRequest solutionRequest) {
		if (solutionRequest.serialNumber() == null) {
			throw new SolutionException(SOLUTION_SERIAL_NUMBER_NOT_FOUND);
		}
		return solutionRequest.serialNumber();
	}
	/**
	 * SolutionData 객체에서 event data를 추출합니다.
	 * @param solutionRequest solutionRequestDto 객체
	 * @return solutionRequestDto.eventData() event data
	 * @throws IllegalArgumentException solutionRequestDto가 null인 경우
	 */
	public static List<SolutionRequest.EventDataDto> extractSolutionEvents(SolutionRequest solutionRequest) {
		if (solutionRequest.eventData() == null) {
			throw new SolutionException(SOLUTION_EVENT_DATA_NOT_FOUND);
		}
		return solutionRequest.eventData();
	}
}
