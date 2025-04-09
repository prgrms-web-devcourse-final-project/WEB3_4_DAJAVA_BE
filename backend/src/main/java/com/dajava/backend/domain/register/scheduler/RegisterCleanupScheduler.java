package com.dajava.backend.domain.register.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.register.repository.RegisterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 솔루션이 끝난 뒤, 일정 시간동안 보관된 Register를 정리하는 스케줄러 클래스
 * @author ChoiHyunSan
 * @since 2025-04-09
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterCleanupScheduler {

	private final RegisterRepository registerRepository;

	@Value("${cleanup.soft-delete-day.register}")
	private long deleteDay;

	@Scheduled(fixedRateString = "${cleanup.scheduler.duration.register}")
	@Transactional
	public void cleanupRegisters() {
		// 직접 삭제하고 삭제된 개수를 반환받음
		int deletedCount = registerRepository.deleteCleanupTargetRegisters(
			LocalDateTime.now().minusDays(deleteDay));

		log.info("오래된 Register 데이터 삭제, 삭제된 데이터 개수 : {}", deletedCount);
	}
}
