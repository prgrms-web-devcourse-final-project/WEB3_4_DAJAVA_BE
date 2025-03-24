package com.dajava.backend.domain.solution;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SolutionService {
	@Value("${DAJAVA_AI_API_KEY}")
	private String apiKey;
	@Value("${DAJAVA_AI_API_URL}")
	private String apiUrl;

	public String getAISolution(String dummy) {
		WebClient client = WebClient.builder()
			.baseUrl(apiUrl)
			.defaultHeader("Content-Type", "application/json")
			.build();
		String requestBody = String.format("{\n" +
			"  \"contents\": [\n" +
			"    {\n" +
			"      \"parts\": [\n" +
			"        {\"text\": \"%s\"}\n" +
			"      ]\n" +
			"    }\n" +
			"  ]\n" +
			"}", dummy);

		Mono<String> response = client.post()
			.uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
			.bodyValue(requestBody)
			.retrieve()
			.bodyToMono(String.class);

		String result = response.block();
		System.out.println("Gemini AI 응답: " + result);
		return result;
	}
}


