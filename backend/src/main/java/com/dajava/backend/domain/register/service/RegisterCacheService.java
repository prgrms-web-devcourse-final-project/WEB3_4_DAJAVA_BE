package com.dajava.backend.domain.register.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 백엔드 서버 시작시 현재 날짜 기준으로 서비스 진행중인 Register 정보를 캐싱 리스트에 캐싱합니다.
 */
@Service
@Slf4j
public class RegisterCacheService {
	private final RegisterRepository registerRepository;

	@Getter
	private List<Register> registerCache;

	public RegisterCacheService(RegisterRepository registerRepository) {
		this.registerRepository = registerRepository;
	}

	public void refreshCache() {
		LocalDateTime now = LocalDateTime.now();
		this.registerCache = registerRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(now, now);
		log.info("Register 캐시 갱신 완료 - {} 건", registerCache.size());
	}
}
