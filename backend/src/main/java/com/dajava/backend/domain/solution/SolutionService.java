package com.dajava.backend.domain.solution;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

/**
 * Gemini 솔루션 활용을 위한 서비스 클래스
 * @author jhon S, sungkibum
 * @since 2025-03-24
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SolutionService {
	@Value("${DAJAVA_AI_API_KEY}")
	private String apiKey;
	@Value("${DAJAVA_AI_API_URL}")
	private String apiUrl;

	@Autowired
	private final SolutionRepository solutionRepository;

	/**
	 * 컨트롤러에서 제공받은 파라미터를 활용해 Gemini에 답변을 요청하는 메서드
	 * @param refineData
	 * @return result(response.block())
	 * @author jhon S, sungkibum
	 * @since 2025-03-24
	 */
	public Mono<SolutionResponseDto> getAISolution(String refineData) {
		WebClient client = WebClient.builder()
			.baseUrl(apiUrl)
			.defaultHeader("Content-Type", "application/json")	//요청 본문(requestBody) 형식 지정
			.build();

		//요청 본문(JSON 형식)
		//Gemini API 요청 본문 형식 준수
		String requestBody = String.format("{\n" +
			"  \"contents\": [\n" +
			"    {\n" +
			"      \"parts\": [\n" +
			"        {\"text\": \"%s\"}\n" +
			"      ]\n" +
			"    }\n" +
			"  ]\n" +
			"}", refineData);

		Mono<String> response = client.post()	//POST 메서드를 사용하여 API에 데이터를 전달 후 응답 대기
			.uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
			.bodyValue(requestBody)
			.retrieve()
			//TODO: 원래 JSON인 데이터를 String으로 형변환되어 Swagger에서 JSON 형태로 보이는 현상 해결 필요
			//		=> DTO에 형식 지정해서 DTO로 받아와야 함
			.bodyToMono(String.class);

		String result = response.block();
		// JSON 파싱 및 contents 추출
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(result);
			// 필요한 값 추출
			String text = rootNode.at("/candidates/0/content/parts/0/text").asText();
			if (text != null) {
				String contents = text.toString();
				SolutionEntity solutionEntity = new SolutionEntity();
				solutionEntity.setText(contents);
				solutionRepository.save(solutionEntity);
				log.info("Gemini AI 응답 성공, DB 저장 완료!");
			} else {
				log.error("Gemini AI 응답에 'contents' 필드가 없습니다.");
			}
		} catch (IOException e) {
			log.error("Gemini AI 응답 JSON 파싱 오류: " + e.getMessage());
		} catch (Exception e){
			log.error("Gemini AI 응답 처리 중 오류 발생: " + e.getMessage());
		}
		return result;
	}


	/**
	 * 컨트롤러에서 제공받은 파라미터를 활용해 Gemini에 답변을 요청하는 메서드 (Flux 적용)
	 * @param refineData
	 * @return Flux<String> (비동기 응답 스트림)
	 */
	public Flux<String> getAISolutions(String refineData) {
		WebClient client = WebClient.builder()
			.baseUrl(apiUrl)
			.defaultHeader("Content-Type", "application/json")
			.build();

		return client.post()
			.uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
			.bodyValue(refineData) // refineData 자체가 멀티턴 형식의 JSON이라고 가정
			.retrieve()
			.bodyToFlux(String.class)
			.doOnNext(response -> log.info("Gemini AI 응답 (멀티턴): " + response));
	}
}