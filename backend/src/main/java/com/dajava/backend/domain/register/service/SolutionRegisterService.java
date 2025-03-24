package com.dajava.backend.domain.register.service;

import static com.dajava.backend.domain.register.constant.SolutionRegisterConstant.*;
import static com.dajava.backend.domain.register.converter.SolutionConverter.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.register.dto.SolutionCreateRequest;
import com.dajava.backend.domain.register.dto.SolutionCreateResponse;
import com.dajava.backend.domain.register.entity.Order;
import com.dajava.backend.domain.register.entity.Solution;
import com.dajava.backend.domain.register.implement.SolutionRegisterValidator;
import com.dajava.backend.domain.register.repository.OrderRepository;
import com.dajava.backend.domain.register.repository.SolutionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SolutionRegisterService
 * 솔루션 관련 비즈니스 로직을 처리하는 클래스
 *
 * @author ChoiHyunSan
 * @since 2025-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SolutionRegisterService {

	private final SolutionRepository solutionRepository;
	private final OrderRepository orderRepository;
	private final SolutionRegisterValidator solutionValidator;

	@Transactional
	public SolutionCreateResponse createSolution(final SolutionCreateRequest request) {

		solutionValidator.validate(request);

		Solution newSolution = solutionRepository.save(Solution.create(request, DEFAULT_SOLUTION_DURATION));
		Order newOrder = orderRepository.save(Order.create(request.email(), request.url()));

		log.info("Solution 엔티티 생성 : {} ", newSolution);
		log.info("Order 엔티티 생성 : {} ", newOrder);

		return toSolutionCreateResponse(newSolution);
	}
}
