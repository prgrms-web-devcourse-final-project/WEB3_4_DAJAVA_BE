package com.dajava.backend.domain.solution;

import java.util.List;

public class SolutionUtils {
	/**
	 * 사용자 세션 데이터를 기반으로 UI/UX 개선점을 제안하는 프롬프트를 생성합니다.
	 * @param sessionDatas 사용자 세션 데이터
	 * @return 생성된 프롬프트 문자열
	 */
	public static String refinePrompt(List<SolutionRequestDto> sessionDatas) {
		return String.format("다음 사용자 세션 데이터를 분석하여 UI/UX 개선점을 제안해주세요: %s", sessionDatas);
	}

	/**
	 * 프롬프트를 바탕으로 AI에게 전달할 데이터 형식을 생성합니다.
	 * @param prompt UI/UX 개선을 위한 프롬프트
	 * @return AI 모델에게 전달할 데이터 문자열
	 */
	public static String buildRefineData(String prompt) {
		return String.format("{\"contents\": [{\"parts\": [{\"text\": \"%s\"}]}]}", prompt);
	}
}
