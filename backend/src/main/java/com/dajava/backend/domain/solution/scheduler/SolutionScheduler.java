package com.dajava.backend.domain.solution.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;

import com.dajava.backend.domain.solution.dto.SolutionRequestDto;
import com.dajava.backend.domain.solution.service.SolutionServiceImpl;
import com.dajava.backend.domain.solution.controller.SolutionController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AISolution 데이터 도출을 위해 돌아가는 스케줄러 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SolutionScheduler {

	private final RegisterRepository registerRepository;
	private final SolutionController solutionController;
	private final SolutionServiceImpl solutionServiceImpl;

	@Scheduled(cron = "0 0 0 * * *")	//매일 자정(00:00)에 실행
	public void processExpiredRegisters() {
		List<Register> expiredRegisters = registerRepository.findByIsServiceExpiredTrue();

		for (Register register : expiredRegisters) {
			try {
				SolutionData solutionData = solutionServiceImpl.getSolutionData(register.getSerialNumber());
				if (solutionData != null) {
					SolutionRequestDto solutionRequestDto = SolutionRequestDto.from(solutionData);
					solutionController.getUXSolution(solutionRequestDto);
					log.info("SolutionScheduler 동작 시리얼 번호: {}", register.getSerialNumber());
				} else {
					log.info("SolutionData == null 시리얼 번호: {}", register.getSerialNumber());
				}

			} catch (Exception e) {
				log.error("Error processing expired register: {}", register.getSerialNumber(), e);
			}
		}
	}
}
