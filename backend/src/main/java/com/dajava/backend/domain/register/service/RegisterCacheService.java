package com.dajava.backend.domain.register.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 백엔드 서버 시작시 현재 날짜 기준으로 서비스 진행중인 Register 의 serialNumber 를 캐싱 Set 에 캐싱합니다.
 * @author Metronon
 * @since 2025-03-27
 */
@Service
@Slf4j
public class RegisterCacheService {
	private final RegisterRepository registerRepository;

	@Getter
	private Set<String> serialNumberCache;

	public RegisterCacheService(RegisterRepository registerRepository) {
		this.registerRepository = registerRepository;
		this.serialNumberCache = Collections.newSetFromMap(new ConcurrentHashMap<>());
	}

	public void refreshCache() {
		LocalDateTime now = LocalDateTime.now();
		List<Register> activeRegisters = registerRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(now,
			now);

		Set<String> newCache = new HashSet<>();
		for (Register register : activeRegisters) {
			newCache.add(register.getSerialNumber());
		}

		// 로그 요청 테스트용 memberSerialNumber
		newCache.add("5_team_testSerial");

		this.serialNumberCache = Collections.newSetFromMap(new ConcurrentHashMap<>());
		this.serialNumberCache.addAll(newCache);

		log.info("Register serialNumber 캐시 갱신 완료 - {} 건", serialNumberCache.size());
	}

	/**
	 * 테스트용: 존재하는 모든 Register 의 serialNumber 를 캐시에 담아 반환하는 메서드.
	 * (진행중인 Register 만이 아니라 전체를 로드함)
	 */
	public Set<String> refreshCacheAll() {
		List<Register> allRegisters = registerRepository.findAll();

		Set<String> newCache = new HashSet<>();
		for (Register register : allRegisters) {
			newCache.add(register.getSerialNumber());
		}

		this.serialNumberCache = Collections.newSetFromMap(new ConcurrentHashMap<>());
		this.serialNumberCache.addAll(newCache);

		log.info("모든 Register serialNumber 캐시 갱신 완료 - {} 건", serialNumberCache.size());

		return this.serialNumberCache;
	}

	public boolean isValidSerialNumber(String serialNumber) {
		return serialNumberCache.contains(serialNumber);
	}
}
