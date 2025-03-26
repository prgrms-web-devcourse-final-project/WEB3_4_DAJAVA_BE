package com.dajava.backend.domain.solution;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class SolutionService {

	private final SolutionRepository solutionRepository;
	private final WebClient geminiWebClient;

	public Mono<SolutionResponseDto> getAISolutions(String refineData) {


		return geminiWebClient.post()
			.uri(uriBuilder -> uriBuilder.queryParam("key", "your-api-key").build())
			.bodyValue(refineData)
			.retrieve()
			.bodyToMono(GeminiAPIResponseDto.class)
			.map(apiResponse -> {
				// GeminiApiResponseDto에서 필요한 데이터 추출
				String text = extractTextFromApiResponse(apiResponse);

				SolutionEntity solutionEntity = SolutionEntity.builder()
					.text(text)
					.build();

				solutionRepository.save(solutionEntity); // 데이터베이스 저장

				return SolutionResponseDto.fromEntity(solutionEntity);
				}
			);
	}
	private String extractTextFromApiResponse(GeminiAPIResponseDto apiResponse) {
		// GeminiApiResponseDto에서 text 추출 로직 구현
		// 예시: apiResponse.getCandidates().get(0).getContent().getParts().get(0).getText();
		if (apiResponse != null && apiResponse.getCandidates() != null && !apiResponse.getCandidates().isEmpty()) {
			Map<String, Object> content = (Map<String, Object>) apiResponse.getCandidates().get(0).get("content");
			if (content != null) {
				List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
				if (parts != null && !parts.isEmpty()) {
					return (String) parts.get(0).get("text");
				}
			}
		}
		return "No text found in API response"; // 또는 예외 처리
	}
}
