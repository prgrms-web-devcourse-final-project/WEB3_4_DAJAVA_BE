package com.dajava.backend.domain.register.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.register.service.RegisterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterExpireScheduler {

	private final RegisterRepository registerRepository;

	@Scheduled(cron = "0 0 0 * * *")
	@Transactional
	public void registerExpire() {
		// 만료 대상 Register를 조회하여 처리한다.
		registerRepository.findExpiredTarget(LocalDateTime.now()).forEach(Register::expire);
	}
}
