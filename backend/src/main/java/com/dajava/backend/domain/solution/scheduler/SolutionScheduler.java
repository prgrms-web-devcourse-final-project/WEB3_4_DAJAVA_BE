package com.dajava.backend.domain.solution.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;
import com.dajava.backend.domain.event.es.repository.SolutionEventDocumentRepository;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.solution.controller.SolutionController;
import com.dajava.backend.domain.solution.dto.SolutionRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 솔루션을 도출하기 위한 API(getUXSolution)를 스케줄러로 동작시키는 코드
 * @author sungkibum
 * @since 2025-04-01
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SolutionScheduler {

	private final RegisterRepository registerRepository;
	private final SolutionController solutionController;
	private final SolutionEventDocumentRepository solutionEventDocumentRepository;

	@Scheduled(cron = "0 0 0 * * *")    //매일 자정(00:00)에 실행
	public void processExpiredRegisters() {
		//추후 등록된 레지스터 정리하는 기능 필요
		List<Register> expiredRegisters = registerRepository.findByIsServiceExpiredTrue();

		for (Register register : expiredRegisters) {
			try {
				// 이미 솔루션이 완료된 상태면 무시함.
				if (register.isSolutionComplete()) {
					continue;
				}

				List<SolutionEventDocument> solutionEventDocumentList = solutionEventDocumentRepository
					.findBySerialNumberAndIsOutlier(register.getSerialNumber(), true);

				if (solutionEventDocumentList.isEmpty()) {
					log.info("No session data for register: {}", register.getSerialNumber());
				}
				SolutionRequest solutionRequest = SolutionRequest.from(register.getSerialNumber(),
					solutionEventDocumentList);
				solutionController.getUXSolution(solutionRequest);
				log.info("Processed expired register: {}", register.getSerialNumber());

			} catch (Exception e) {
				log.error("Error processing expired register: {}", register.getSerialNumber(), e);
			}
		}
	}
}
