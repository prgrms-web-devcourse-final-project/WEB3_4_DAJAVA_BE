package com.dajava.backend.domain.solution;

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

	public Mono<SolutionDto> getAISolutions(String refineData) {
		return geminiWebClient.post()
			.uri(uriBuilder -> uriBuilder.queryParam("key", "your-api-key").build())
			.bodyValue(refineData)
			.retrieve()
			.bodyToMono(SolutionDto.class)
			.flatMap(response -> {
				SolutionEntity solutionEntity = SolutionEntity.builder()
					.text(// Todo..)
					.build();
				log.info("Gemini AI 응답: {}", response);
				return solutionRepository.save(solutionEntity);
			});
	}
}
