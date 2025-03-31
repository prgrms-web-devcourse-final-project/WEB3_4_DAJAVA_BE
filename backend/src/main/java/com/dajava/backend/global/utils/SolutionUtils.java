package com.dajava.backend.global.utils;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.util.List;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;
import com.dajava.backend.domain.solution.dto.SolutionRequestDto;
import com.dajava.backend.domain.solution.exception.SolutionException;

public final class SolutionUtils {
	/**
	 * 사용자 세션 데이터를 기반으로 UI/UX 개선점을 제안하는 프롬프트를 생성합니다.
	 * @param solutionRequestDto 사용자 세션 데이터
	 * @return 생성된 프롬프트 문자열
	 */
	public static String refinePrompt(SolutionRequestDto solutionRequestDto) {
		return String.format("다음 사용자 세션 데이터를 분석하여 UI/UX 개선점을 제안해주세요: %s", solutionRequestDto);
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
	 * requestDto를 바탕으로 serialNumber를 추출합니다.
	 * @param text requestDto를 string화
	 * @return serialNumber 추출
	 */
	public static String extractSerialNumber(String text) {
		int startIdx = text.indexOf("serialNumber=") + "serialNumber=".length();
		int endIdx = text.indexOf(",", startIdx);
		if (endIdx == -1) {
			endIdx = text.length();
		}
		return text.substring(startIdx, endIdx).trim();
	}
	/**
	 * SolutionData 객체에서 serialNumber를 추출합니다.
	 * @param solutionData // SolutionData 객체
	 * @return serialNumber
	 * @throws IllegalArgumentException // SolutionData가 null인 경우 예외 발생
	 */
	public static String extractsSerialNumber(SolutionData solutionData) {
		if (solutionData == null) {
			throw new SolutionException(SERIAL_NUMBER_DATA_NULL);
		}
		return solutionData.getSerialNumber();
	}

	public static List<SolutionEvent> extractSolutionEvents(SolutionData solutionData) {
		if (solutionData == null) {
			throw new SolutionException(SERIAL_NUMBER_DATA_NULL);
		}
		return solutionData.getSolutionEvents();
	}
}
