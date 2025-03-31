package com.dajava.backend.domain.solution;

import static com.dajava.backend.domain.solution.SolutionUtils.*;
import static com.dajava.backend.global.exception.ErrorCode.*;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.global.utils.PasswordUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Gemini 솔루션 활용을 위한 서비스 클래스
 * @author jhon S, sungkibum
 * @since 2025-03-24
 */
@Service
@RequiredArgsConstructor
public class SolutionService {
	@Value("${dajava.apiKey}")
	private String apiKey;
	@Value("${dajava.url}")
	private String apiUrl;

	@Autowired
	private final SolutionRepository solutionRepository;

	@Autowired
	private final RegisterRepository registerRepository;

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
						return Mono.error(new RegisterException(SERIAL_NUMBER_NOT_FOUND));
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
						return Mono.error(new RegisterException(SOLUTION_TEXT_EMPTY));
					}
				} catch (IOException e) {
					return Mono.error(new RegisterException(SOLUTION_PARSING_ERROR));
				} catch (Exception e) {
					return Mono.error(new RegisterException(SOLUTION_RESPONSE_ERROR));
				}
			});
	}

	/**
	 * 특정 시리얼 넘버(serialNumber)와 비밀번호(password)에 해당하는 솔루션 정보를 조회하는 메서드입니다.
	 * @param serialNumber 조회할 시리얼 넘버
	 * @param password 인증을 위한 비밀번호 (현재 사용되지 않음)
	 * @return SolutionInfoResponse 솔루션 정보 응답 객체
	 * @throws RegisterException 시리얼 넘버를 찾을 수 없거나, 비밀번호가 일치하지 않거나, 솔루션 정보가 없을 경우 발생
	 */
	public SolutionInfoResponse getSolutionInfo(String serialNumber, String password) {
		Register findRegister = Optional.ofNullable(registerRepository.findBySerialNumber(serialNumber))
			.orElseThrow(() -> new RegisterException(INVALID_SERIAL_NUMBER));
		PasswordUtils passwordUtils = new PasswordUtils();
		//해시화된 password 검증로직
		if (!passwordUtils.verifyPassword(password, findRegister.getPassword())) {
			throw new RegisterException(INVALID_PASSWORD);
		}
		SolutionEntity solutionEntity = solutionRepository.findByRegister(findRegister)
			.orElseThrow(() -> new RegisterException(SOLUTION_NOT_FOUND));
		return new SolutionInfoResponse(solutionEntity.getText());
	}
}
