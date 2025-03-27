package com.dajava.backend.domain.register.implement;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.register.dto.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.RegisterModifyRequest;
import com.dajava.backend.domain.register.dto.RegistersInfoRequest;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.domain.register.repository.RegisterRepository;

import lombok.RequiredArgsConstructor;

/**
 * RegisterValidator
 * 솔루션 관련 데이터에 대한 validate 역할을 수행하는 클래스
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Component
@RequiredArgsConstructor
public class RegisterValidator {

	private final RegisterRepository solutionRepository;

	/**
	 * 솔루션 등록 요청 검증 메서드
	 * @param request 솔루션 등록 요청 DTO
	 */
	public void validateCreateRequest(final RegisterCreateRequest request) {

		// Request Data 유효성 검증
		if (!isValidEmail(request.email()) || !isValidDate(request.startDate(), request.endDate())) {
			throw new RegisterException(INVALID_REGISTER_REQUEST);
		}

		// 2. Url 등록 가능 여부 체크
		if (!solutionRepository.checkUrlAvailability(request.url(), LocalDateTime.now().minusDays(7))) {
			throw new RegisterException(ALREADY_REGISTER_URL);
		}
	}

	/**
	 * 솔루션 수정 요청 검증 메서드
	 *
	 * @param request    솔루션 수정 요청 DTO
	 * @param solutionId 대상 솔루션 ID
	 */
	public Register validateModifyRequest(RegisterModifyRequest request, Long solutionId) {

		// 수정 사항에 대한 구체적인 방안이 나오지 않았으므로 아직은 체크하지 않음
		if (request.solutionCompleteDate().getMinute() != 0 || request.solutionCompleteDate().getSecond() != 0) {
			throw new RegisterException(INVALID_REGISTER_REQUEST);
		}

		return solutionRepository.findById(solutionId).orElseThrow(
			() -> new RegisterException(SOLUTION_NOT_FOUND)
		);
	}

	/**
	 * 솔루션 삭제 요청 검증 메서드
	 *
	 * @param solutionId 대상 솔루션 ID
	 * @return 조회된 솔루션
	 */
	public Register validateDeleteRequest(Long solutionId) {
		return solutionRepository.findById(solutionId).orElseThrow(
			() -> new RegisterException(SOLUTION_NOT_FOUND)
		);
	}

	/**
	 * 솔루션 리스트 조회 요청 검증 메서드
	 *
	 * @param request 페이징 정보
	 */
	public void validateInfoRequest(RegistersInfoRequest request) {
		if (request.pageNum() < 0 || request.pageSize() < 1) {
			throw new RegisterException(INVALID_REGISTER_REQUEST);
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

		// 시간 단위로 받아야 한다.
		if (start.getMinute() != 0 || end.getMinute() != 0 || start.getSecond() != 0 || end.getSecond() != 0) {
			return false;
		}

		// end 시간이 start 시간보다 같거나 앞에 있으면 안된다.
		if (end.isEqual(start) || end.isBefore(start)) {
			return false;
		}

		return true;
	}
}
