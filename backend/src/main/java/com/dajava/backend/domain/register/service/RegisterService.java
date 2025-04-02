package com.dajava.backend.domain.register.service;

import static com.dajava.backend.domain.register.converter.RegisterConverter.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.register.RegisterInfo;
import com.dajava.backend.domain.register.converter.RegisterConverter;
import com.dajava.backend.domain.register.dto.register.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.register.RegisterCreateResponse;
import com.dajava.backend.domain.register.dto.register.RegisterDeleteResponse;
import com.dajava.backend.domain.register.dto.register.RegisterModifyRequest;
import com.dajava.backend.domain.register.dto.register.RegisterModifyResponse;
import com.dajava.backend.domain.register.dto.register.RegistersInfoRequest;
import com.dajava.backend.domain.register.dto.register.RegistersInfoResponse;
import com.dajava.backend.domain.register.entity.Order;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.exception.RegisterException;
import com.dajava.backend.domain.register.implement.RegisterValidator;
import com.dajava.backend.domain.register.repository.OrderRepository;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RegisterService
 * 솔루션 관련 비즈니스 로직을 처리하는 클래스
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {

	private final RegisterRepository registerRepository;
	private final OrderRepository orderRepository;
	private final RegisterValidator registerValidator;

	/**
	 * 서비스 Register 생성 메서드
	 *
	 * @param request RegisterCreateRequest (DTO)
	 * @return RegisterCreateResponse (DTO)
	 */
	@Transactional
	public RegisterCreateResponse createRegister(final RegisterCreateRequest request) {
		registerValidator.validateCreateRequest(request);

		Register newRegister = registerRepository.save(Register.create(request));
		Order newOrder = orderRepository.save(Order.create(request.email(), request.url()));

		log.info("Register 엔티티 생성 : {} ", newRegister);
		log.info("Order 엔티티 생성 : {} ", newOrder);

		return toRegisterCreateResponse(newRegister);
	}

	/**
	 * Register 수정 메서드
	 * Register 수정 가능 여부를 파악한 후, 수정한다.
	 *
	 * @param request RegisterModifyRequest (DTO)
	 * @param solutionId 대상 솔루션 ID
	 * @return RegisterModifyResponse (DTO)
	 */
	@Transactional
	public RegisterModifyResponse modifySolution(RegisterModifyRequest request, Long solutionId) {

		Register targetSolution = registerValidator.validateModifyRequest(request, solutionId);
		targetSolution.updateEndDate(request.solutionCompleteDate());

		log.info("Solution endDate 수정 성공, Target Solution : {}, New endDate : {}",
			solutionId, targetSolution.getEndDate());
		return RegisterModifyResponse.create();
	}

	/**
	 * Register 삭제 메서드 (TODO)
	 * **** 현재 스프린트 상 껍데기만 존재 *****
	 *
	 * @param solutionId 대상 솔루션 ID
	 * @return RegisterDeleteResponse (DTO)
	 */
	@Transactional
	public RegisterDeleteResponse deleteSolution(Long solutionId) {
		Register targetSolution = registerValidator.validateDeleteRequest(solutionId);

		log.info("Solution endDate 삭제 성공, Target Solution : {} ", solutionId);
		return RegisterDeleteResponse.create();
	}

	/**
	 * Register 리스트 조회 메서드
	 *
	 * @param request RegisterInfoRequest (DTO)
	 * @return RegistersInfoResponse (DTO)
	 */
	@Transactional(readOnly = true)
	public RegistersInfoResponse getRegisterList(RegistersInfoRequest request) {
		registerValidator.validateInfoRequest(request);

		Pageable pageable = PageRequest.of(request.pageNum(), request.pageSize());
		List<RegisterInfo> registerInfos = registerRepository.findAll(pageable).stream()
			.map(RegisterConverter::toRegisterInfo)
			.toList();

		log.info("Solution 등록 리스트를 조회합니다. PageNum: {}, PageSize: {}, Search Count: {}",
			request.pageNum(), request.pageSize(), registerInfos.size());

		return RegistersInfoResponse.create(registerInfos);
	}

	/**
	 * Register pageCapture 수정 메서드
	 *
	 * @param serialNumber 각 세션에서 가지고 있는 솔루션 식별자 입니다.
	 * @param captureData 분리되어 전송된 캡쳐 데이터를 합친 Lob 입니다.
	 */
	@Transactional
	public void modifyPageCaptureIfAbsent(String serialNumber, String captureData) {
		Register register = registerRepository.findBySerialNumber(serialNumber)
			.orElseThrow(() -> new RegisterException(ErrorCode.REGISTER_NOT_FOUND));

		if (register.getPageCapture() == null || register.getPageCapture().isEmpty()) {
			register.updatePageCapture(captureData);
			registerRepository.save(register);
		}
	}
}
