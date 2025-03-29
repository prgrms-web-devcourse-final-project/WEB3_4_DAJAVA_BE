package com.dajava.backend.domain.solution;

import java.util.List;

import com.dajava.backend.domain.event.entity.SolutionData;

public class SolutionUtils {
	/**
	 * 사용자 세션 데이터를 기반으로 UI/UX 개선점을 제안하는 프롬프트를 생성합니다.
	 * @param sessionDatas 사용자 세션 데이터
	 * @return 생성된 프롬프트 문자열
	 */
	public static String refinePrompt(SolutionData sessionData) {
		return String.format("다음 사용자 세션 데이터를 분석하여 UI/UX 개선점을 제안해주세요: %s", sessionData);
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
	// 텍스트에서 serialNumber 추출하는 함수
	public static String extractSerialNumber(String text) {
		int startIdx = text.indexOf("serialNumber=") + "serialNumber=".length();
		int endIdx = text.indexOf(",", startIdx);
		if (endIdx == -1) {
			endIdx = text.length();
		}
		return text.substring(startIdx, endIdx).trim();
	}
}
