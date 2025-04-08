package com.dajava.backend.domain.solution.service;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import reactor.core.scheduler.Schedulers;
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

	/**
	 * Gemini API 통신 후 UI 개선 솔루션을 받기 위한 메서드
	 * @param refineData // 요청할 데이터
	 * @param serialNumber // 신청자에게 제공된 시리얼 넘버
	 * @return Mono<SolutionResponse>
	 */
	@Autowired
	private PlatformTransactionManager transactionManager;

	@Override
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

					return Mono.fromCallable(() -> {
						TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
						return txTemplate.execute(status -> {
							Register register = registerRepository.findBySerialNumber(serialNumber)
								.orElseThrow(() -> new SolutionException(SOLUTION_SERIAL_NUMBER_NOT_FOUND));

							Solution solution = Solution.builder()
								.text(text)
								.register(register)
								.build();

							solutionRepository.save(solution);
							register.setSolutionComplete(true);
							registerRepository.save(register);

							return new SolutionResponse(text);
						});
					}).subscribeOn(Schedulers.boundedElastic());

				} catch (IOException e) {
					return Mono.error(new SolutionException(SOLUTION_PARSING_ERROR));
				} catch (Exception e) {
					return Mono.error(new SolutionException(SOLUTION_RESPONSE_ERROR));
				}
			});
	}


	/**
	 * 솔루션 정보 조회 메서드
	 * @param serialNumber // 신청자에게 제공된 시리얼 넘버
	 * @param password // 신청자가 작성한 비밀번호
	 * @return SolutionInfoResponse
	 */
	@Override
	public SolutionInfoResponse getSolutionInfo(String serialNumber, String password) {
		Register findRegister = registerRepository.findBySerialNumber(serialNumber)
			.orElseThrow(() -> new SolutionException(SOLUTION_SERIAL_NUMBER_INVALID));
		//해시화된 password 검증로직
		if (!PasswordUtils.verifyPassword(password, findRegister.getPassword())) {
			throw new SolutionException(SOLUTION_PASSWORD_INVALID);
		}
		Solution solution = solutionRepository.findByRegister(findRegister)
			.orElseThrow(() -> new SolutionException(SOLUTION_NOT_FOUND));
		return new SolutionInfoResponse(solution.getText());

	}

	/**
	 * 이상치 데이터라고 판단된 데이터(SolutionData)들을 조회하는 메서드
	 * @param serialNumber
	 * @return SolutionData
	 */
	@Override
	public SolutionData getSolutionData(String serialNumber) {
		return solutionDataRepository.findBySerialNumber(serialNumber)
			.orElseThrow(() -> new SolutionException(SOLUTION_DATA_NOT_FOUND));
	}
}
