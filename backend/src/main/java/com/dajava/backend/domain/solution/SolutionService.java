package com.dajava.backend.domain.solution;

import org.springframework.stereotype.Service;

@Service
public class SolutionService {

	//TODO: API Key env파일화
	private final String apiKey = "AIzaSyBfPXGPaKsMdXTQGDiegusqet3AvJXXShE";  // OpenAI API 키
	private final String apiUrl = "https://api.openai.com/v1/completions";  // OpenAI API URL

	public String getAISolution(String logData) {
		WebClient client = WebClient.builder()
			.baseUrl(apiUrl)
			.defaultHeader("Authorization", "Bearer " + apiKey)
			.build();

		String prompt = "다음 로그 데이터를 분석하여 UX 개선 솔루션을 제안해줘:\n" + logData;

		Mono<String> response = client.post()
			.bodyValue("{"model":"text-davinci-003","prompt":"" + prompt + "","max_tokens":150}")
			.retrieve()
			.bodyToMono(String.class);

		return response.block();  // 응답을 기다리고 결과를 반환
	}
}