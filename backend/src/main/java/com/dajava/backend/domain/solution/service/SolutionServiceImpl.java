package com.dajava.backend.domain.solution.service;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.repository.SolutionDataRepository;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.solution.dto.SolutionInfoResponse;
import com.dajava.backend.domain.solution.dto.SolutionResponse;
import com.dajava.backend.domain.solution.entity.Solution;
import com.dajava.backend.domain.solution.exception.SolutionException;
import com.dajava.backend.domain.solution.repository.SolutionRepository;
import com.dajava.backend.global.config.GeminiApiConfig;
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

	private final SolutionRepository solutionRepository;

	private final RegisterRepository registerRepository;

	private final SolutionDataRepository solutionDataRepository;

	private final GeminiApiConfig geminiApiConfig;

	@Override
	@Transactional
	public Mono<SolutionResponse> getAISolution(String refineData, String serialNumber) {
		return geminiApiConfig.geminiWebClient().post()
			.uri(uriBuilder -> uriBuilder.queryParam("key", geminiApiConfig.getApiKey()).build())
			.bodyValue(refineData)
			.retrieve()
			.bodyToMono(String.class)
			.flatMap(result -> {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					JsonNode rootNode = objectMapper.readTree(result);
					String text = rootNode.at("/candidates/0/content/parts/0/text").asText();
					Register register = registerRepository.findBySerialNumber(serialNumber);
					if (register == null) {
						return Mono.error(new SolutionException(SOLUTION_SERIAL_NUMBER_NOT_FOUND));
					}
					if (text != null) {

						Solution solution = Solution.builder()
							.text(text)
							.register(register)
							.build();
						solutionRepository.save(solution);
						SolutionResponse solutionResponseDto = new SolutionResponse(text);
						solution.getRegister().setSolutionComplete(true);
						registerRepository.save(register);

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
		//해시화된 password 검증로직
		if (!PasswordUtils.verifyPassword(password, findRegister.getPassword())) {
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
