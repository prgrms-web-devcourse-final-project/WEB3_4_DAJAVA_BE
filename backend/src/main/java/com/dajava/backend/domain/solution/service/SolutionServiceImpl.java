package com.dajava.backend.domain.solution.service;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.repository.SolutionDataRepository;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.solution.dto.SolutionInfoResponse;
import com.dajava.backend.domain.solution.dto.SolutionResponse;
import com.dajava.backend.domain.solution.entity.Solution;
import com.dajava.backend.domain.solution.exception.SolutionException;
import com.dajava.backend.domain.solution.repository.SolutionRepository;
import com.dajava.backend.global.utils.PasswordUtils;
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
@RequiredArgsConstructor

@Slf4j
public class SolutionServiceImpl implements SolutionService {
	@Value("${dajava.apiKey}")
	private String apiKey;
	@Value("${dajava.url}")
	private String apiUrl;

	@Autowired
	private final SolutionRepository solutionRepository;

	@Autowired
	private final RegisterRepository registerRepository;

	@Autowired
	private final SolutionDataRepository solutionDataRepository;

	@Override
	public Mono<SolutionResponse> getAISolution(String refineData, String serialNumber) {
		Register register = registerRepository.findBySerialNumber(serialNumber);
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

					if (register == null) {
						return Mono.error(new SolutionException(SOLUTION_SERIAL_NUMBER_NOT_FOUND));
					}
					if (text != null) {
						Solution solution = new Solution();
						solution.setText(text);
						solution.setRegister(register);
						solutionRepository.save(solution);
						SolutionResponse solutionResponseDto = new SolutionResponse();
						solutionResponseDto.setText(text);
						solutionResponseDto.setRegisterSerialNumber(register.getSerialNumber());

						return Mono.just(solutionResponseDto);
					} else {
						return Mono.error(new SolutionException(SOLUTION_EVENT_DATA_NOT_FOUND));
					}
				} catch (IOException e) {
					return Mono.error(new SolutionException(SOLUTION_PARSING_ERROR));
				} catch (Exception e) {
					return Mono.error(new SolutionException(SOLUTION_RESPONSE_ERROR));
				}
			});
	}

	@Override
	public SolutionInfoResponse getSolutionInfo(String serialNumber, String password) {
		Register findRegister = Optional.ofNullable(registerRepository.findBySerialNumber(serialNumber))
			.orElseThrow(() -> new SolutionException(SOLUTION_SERIAL_NUMBER_INVALID));
		PasswordUtils passwordUtils = new PasswordUtils();
		//해시화된 password 검증로직
		if (!passwordUtils.verifyPassword(password, findRegister.getPassword())) {
			throw new SolutionException(SOLUTION_PASSWORD_INVALID);
		}
		Solution solution = solutionRepository.findByRegister(findRegister)
			.orElseThrow(() -> new SolutionException(SOLUTION_NOT_FOUND));
		return new SolutionInfoResponse(solution.getText());

	}

	@Override
	public SolutionData getSolutionData(String serialNumber) {
		return solutionDataRepository.findBySerialNumber(serialNumber);
	}
}
