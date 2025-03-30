package com.dajava.backend.domain.solution.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.solution.service.SolutionService;
import com.dajava.backend.domain.solution.controller.SolutionController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolutionScheduler {

	private final RegisterRepository registerRepository;
	private final SolutionController solutionController;
	private final SolutionService solutionService;

	@Scheduled(cron = "0 0 0 * * *")	//매일 자정(00:00)에 실행
	public void processExpiredRegisters() {
		LocalDateTime today = LocalDateTime.now();
		List<Register> expiredRegisters = registerRepository.findByEndDateLessThanEqual(today);

		for (Register register : expiredRegisters) {
			try {
				SolutionData solutionData = solutionService.getSolutionData(register.getSerialNumber());
				if (solutionData != null) {
					solutionController.getUXSolution(solutionData);
					log.info("Processed expired register: {}", register.getSerialNumber());
				} else {
					log.info("No session data for register: {}", register.getSerialNumber());
				}

			} catch (Exception e) {
				log.error("Error processing expired register: {}", register.getSerialNumber(), e);
			}
		}
	}
}
