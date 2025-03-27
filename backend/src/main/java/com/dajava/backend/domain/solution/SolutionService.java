package com.dajava.backend.domain.solution;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

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

	@Autowired
	private final RegisterRepository registerRepository;

	// 텍스트에서 serialNumber 추출하는 함수
	private String extractSerialNumber(String text) {
		int startIdx = text.indexOf("serialNumber=") + "serialNumber=".length();
		int endIdx = text.indexOf(",", startIdx);
		if (endIdx == -1) {
			endIdx = text.length();
		}
		return text.substring(startIdx, endIdx).trim();
	}

	/**
	 * 컨트롤러에서 제공받은 파라미터를 활용해 Gemini에 답변을 요청하는 메서드
	 * @param refineData
	 * @return result(response)
	 * @author jhon S, sungkibum
	 * @since 2025-03-24
	 */
	public Mono<SolutionResponseDto> getAISolution(String refineData) {
		WebClient client = WebClient.builder()
			.baseUrl(apiUrl)
			.defaultHeader("Content-Type", "application/json")
			.build();
		return client.post()
			.uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
			.bodyValue(refineData)
			.retrieve()
			.bodyToMono(String.class)
			.flatMap(result -> {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					JsonNode rootNode = objectMapper.readTree(result);
					String text = rootNode.at("/candidates/0/content/parts/0/text").asText();
					String serialNumber = extractSerialNumber(refineData);
					Register register = registerRepository.findBySerialNumber(serialNumber);
					if (register == null) {
						return Mono.error(new IllegalArgumentException("해당 serialNumber에 대한 Register를 찾을 수 없습니다."));
					}
					if (text != null) {
						SolutionEntity solutionEntity = new SolutionEntity();
						solutionEntity.setText(text);
						solutionEntity.setRegister(register);
						solutionRepository.save(solutionEntity);
						SolutionResponseDto solutionResponseDto = new SolutionResponseDto();
						solutionResponseDto.setText(text);
						solutionResponseDto.setRegisterSerialNumber(register.getSerialNumber());
						return Mono.just(solutionResponseDto);
					} else {
						log.error("Gemini AI 응답에 'contents' 필드가 없습니다.");
						return Mono.error(new RuntimeException("Gemini AI 응답에 'contents' 필드가 없습니다."));
					}
				} catch (IOException e) {
					log.error("Gemini AI 응답 JSON 파싱 오류: " + e.getMessage());
					return Mono.error(new RuntimeException("Gemini AI 응답 JSON 파싱 오류: " + e.getMessage()));
				} catch (Exception e) {
					log.error("Gemini AI 응답 처리 중 오류 발생: " + e.getMessage());
					return Mono.error(new RuntimeException("Gemini AI 응답 처리 중 오류 발생: " + e.getMessage()));
				}
			});
	}


	public SolutionInfoResponse getSolutionInfo(String serialNumber, String password) {
		Register findRegister = registerRepository.findBySerialNumber(serialNumber);
		if(findRegister == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,"시리얼 넘버가 없습니다.");
		}
		Optional<SolutionEntity> opSolutionEntity = solutionRepository.findByRegister(findRegister);
		if (opSolutionEntity.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "솔루션을 찾을 수 없습니다.");
		} else {
			SolutionEntity solutionEntity = opSolutionEntity.get();
			return new SolutionInfoResponse(solutionEntity.getText());
		}
	}
}