package com.dajava.backend.domain.solution;

import java.util.Map;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SolutionService {
	private final String apiKey = "YOUR_OPENAI_API_KEY";
	private final String apiUrl = "https://api.openai.com/v1/completions";

	public String getAISolution(String logData) {
		WebClient client = WebClient.builder()
			.baseUrl(apiUrl)
			.defaultHeader("Authorization", "Bearer " + apiKey)
			.build();

		Map<String, Object> requestBody = Map.of(
			"model", "text-davinci-003",
			"prompt", "다음 로그 데이터를 분석하여 UX 개선 솔루션을 제안해줘:\n" + logData,
			"max_tokens", 150
		);

		Mono<String> response = client.post()
			.bodyValue(requestBody)
			.retrieve()
			.bodyToMono(String.class);
		return response.block();
	}
}
