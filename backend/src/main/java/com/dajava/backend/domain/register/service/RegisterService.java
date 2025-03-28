package com.dajava.backend.domain.register.service;

import static com.dajava.backend.domain.register.constant.RegisterConstant.*;
import static com.dajava.backend.domain.register.converter.RegisterConverter.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.register.dto.RegisterCreateRequest;
import com.dajava.backend.domain.register.dto.RegisterCreateResponse;
import com.dajava.backend.domain.register.entity.Order;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.implement.RegisterValidator;
import com.dajava.backend.domain.register.repository.OrderRepository;
import com.dajava.backend.domain.register.repository.RegisterRepository;

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

	@Transactional
	public RegisterCreateResponse createRegister(final RegisterCreateRequest request) {

		registerValidator.validate(request);

		Register newRegister= registerRepository.save(Register.create(request, DEFAULT_REGISTER_DURATION));
		Order newOrder = orderRepository.save(Order.create(request.email(), request.url()));

		log.info("Register 엔티티 생성 : {} ", newRegister);
		log.info("Order 엔티티 생성 : {} ", newOrder);

		return toRegisterCreateResponse(newRegister);
	}
}
