package com.dajava.backend.domain.event.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.converter.PointerEventConverter;
import com.dajava.backend.domain.event.entity.PointerClickEvent;
import com.dajava.backend.domain.event.entity.PointerMoveEvent;
import com.dajava.backend.domain.event.entity.PointerScrollEvent;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.entity.SolutionData;
import com.dajava.backend.domain.event.entity.SolutionEvent;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.domain.event.repository.SolutionDataRepository;
import com.dajava.backend.domain.event.scheduler.vaildation.ClickEventAnalyzer;
import com.dajava.backend.domain.event.scheduler.vaildation.MoveEventAnalyzer;
import com.dajava.backend.domain.event.scheduler.vaildation.ScrollEventAnalyzer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventValidateScheduler {

	private final SessionDataRepository sessionDataRepository;
	private final SolutionDataRepository solutionDataRepository;

	private final ClickEventAnalyzer clickEventAnalyzer;
	private final MoveEventAnalyzer moveEventAnalyzer;
	private final ScrollEventAnalyzer scrollEventAnalyzer;

	// 비활성 상태 간주 시간 (10분)
	private static final long VALIDATE_END_SESSION_MS = 10 * 60 * 1000;

	@Transactional
	@Scheduled(fixedRate = VALIDATE_END_SESSION_MS)
	public void endedSessionValidate() {

		// 1. 종료된 세션 가져오기
		List<SessionData> sessionDataList = sessionDataRepository.findEndedSession();

		log.info("SessionData size : {}", sessionDataList.size());

		// 2. 가져온 세션을 반복문으로 처리
		for (SessionData sessionData : sessionDataList) {
			String serialNumber = sessionData.getMemberSerialNumber();
			SolutionData solutionData = SolutionData.create(serialNumber);

			List<PointerClickEvent> clickResult = clickEventAnalyzer.analyze(sessionData);
			List<PointerMoveEvent> moveResult = moveEventAnalyzer.analyze(sessionData);
			List<PointerScrollEvent> scrollResult = scrollEventAnalyzer.analyze(sessionData);

			sessionData.setVerified();

			List<SolutionEvent> solutionEvents = PointerEventConverter.toPointerEvents(clickResult, moveResult,
				scrollResult, solutionData);

			solutionData.addPointerEvents(solutionEvents);

			solutionDataRepository.save(solutionData);
		}
	}

}
