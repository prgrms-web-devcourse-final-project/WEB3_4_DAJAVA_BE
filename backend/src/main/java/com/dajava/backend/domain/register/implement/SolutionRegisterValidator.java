package com.dajava.backend.domain.register.implement;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.register.dto.SolutionCreateRequest;
import com.dajava.backend.domain.register.exception.SolutionException;
import com.dajava.backend.domain.register.repository.SolutionRepository;

import lombok.RequiredArgsConstructor;

/**
 * SolutionRegisterValidator
 * 솔루션 관련 데이터에 대한 validate 역할을 수행하는 클래스
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Component
@RequiredArgsConstructor
public class SolutionRegisterValidator {

	private final SolutionRepository solutionRepository;

	public void validate(final SolutionCreateRequest request) {

		// Request Data 유효성 검증
		if (!isValidEmail(request.email()) || !isValidDate(request.startDate(), request.endDate())) {
			throw new SolutionException(INVALID_SOLUTION_REQUEST);
		}

		// 2. Url 등록 가능 여부 체크
		if (solutionRepository.checkUrlAvailability(request.url(), LocalDateTime.now().minusDays(7))) {
			throw new SolutionException(ALREADY_REGISTER_URL);
		}
	}

	private boolean isValidEmail(final String email) {
		if (email == null || email.isEmpty()) {
			return false;
		}

		// 기본적인 이메일 형식 검증 정규표현식
		// local-part@domain 형식을 확인합니다.
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@"
			+ "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}

	private boolean isValidDate(final LocalDateTime start, final LocalDateTime end) {
		if (start == null || end == null) {
			return false;
		}
		return !end.isBefore(start);
	}
}
