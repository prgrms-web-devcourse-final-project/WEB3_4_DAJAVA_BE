package com.dajava.backend.domain.heatmap.service;

import static com.dajava.backend.global.exception.ErrorCode.*;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.domain.heatmap.dto.HeatmapResponse;
import com.dajava.backend.domain.register.entity.Register;
import com.dajava.backend.domain.register.repository.RegisterRepository;
import com.dajava.backend.domain.solution.exception.SolutionException;
import com.dajava.backend.global.utils.PasswordUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeatmapServiceImpl implements HeatmapService {

	private final RegisterRepository registerRepository;
	private final SessionDataRepository sessionDataRepository;

	@Override
	@Cacheable(value = "heatmapCache", key = "{#serialNumber, #type}")
	@Transactional(readOnly = true)
	public HeatmapResponse getHeatmap(String serialNumber, String password, String type) {
		Register findRegister = registerRepository.findBySerialNumber(serialNumber)
			.orElseThrow(() -> new SolutionException(SOLUTION_SERIAL_NUMBER_INVALID));

		if (!PasswordUtils.verifyPassword(password, findRegister.getPassword())) {
			throw new SolutionException(SOLUTION_PASSWORD_INVALID);
		}

		List<SessionData> sessions = sessionDataRepository.findByMemberSerialNumber(serialNumber)
			.orElseThrow(() -> new SolutionException(SESSION_DATA_NOT_FOUND));

		return createHeatmap(sessions, type);
	}

	@Override
	public HeatmapResponse createHeatmap(List<SessionData> sessions, String type) {

	}
}
