package com.dajava.backend.domain.heatmap.validation;

import org.springframework.stereotype.Component;

@Component
public class UrlEqualityValidator {

	/**
	 * 두 URL 이 같은지 비교하는 메서드.
	 * 예를 들어, "http://www.example.com"과 "https://www.example.com"은 동일한 URL 로 간주
	 *
	 * @param targetUrl   기준 URL
	 * @param candidateUrl 비교할 URL
	 * @return 프로토콜을 무시했을 때 두 URL 이 동일하면 true, 아니면 false
	 */
	public boolean isMatching(String targetUrl, String candidateUrl) {
		if (targetUrl == null || candidateUrl == null) {
			return false;
		}
		String normalizedTarget = removeProtocol(targetUrl);
		String normalizedCandidate = removeProtocol(candidateUrl);
		return normalizedTarget.equalsIgnoreCase(normalizedCandidate);
	}

	/**
	 * URL 문자열에서 "http://" 또는 "https://" 접두사를 제거합니다.
	 *
	 * @param url 비교할 원본 URL
	 * @return 프로토콜이 제거된 URL
	 */
	private String removeProtocol(String url) {
		String lowerCaseUrl = url.toLowerCase();
		if (lowerCaseUrl.startsWith("http://")) {
			return url.substring(7);  // "http://"의 길이
		} else if (lowerCaseUrl.startsWith("https://")) {
			return url.substring(8);  // "https://"의 길이
		} else {
			return url;
		}
	}
}
