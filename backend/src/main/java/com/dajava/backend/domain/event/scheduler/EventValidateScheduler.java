package com.dajava.backend.domain.event.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.SessionData;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.domain.event.scheduler.vaildation.ClickEventAnalyzer;
import com.dajava.backend.domain.event.scheduler.vaildation.MoveEventAnalyzer;
import com.dajava.backend.domain.event.scheduler.vaildation.ScrollEventAnalyzer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventValidateScheduler {

	private final SessionDataRepository sessionDataRepository;

	private final ClickEventAnalyzer clickEventAnalyzer;
	private final MoveEventAnalyzer moveEventAnalyzer;
	private final ScrollEventAnalyzer scrollEventAnalyzer;

	// 비활성 상태 간주 시간 (10분)
	private static final long VALIDATE_END_SESSION_MS = 10 * 60 * 1000;

	@Scheduled(fixedRate = VALIDATE_END_SESSION_MS)
	public void endedSessionValidate() {

		// 1. 종료된 세션 가져오기
		List<SessionData> sessionDataList = sessionDataRepository.findEndedSession();

		log.info("SessionData size : {}", sessionDataList.size());

		// 2. 가져온 세션을 반복문으로 처리
		for (SessionData sessionData : sessionDataList) {
			boolean clickResult = clickEventAnalyzer.analyze(sessionData);
			boolean moveResult = moveEventAnalyzer.analyze(sessionData);
			boolean scrollResult = scrollEventAnalyzer.analyze(sessionData);

			if (clickResult || moveResult || scrollResult) {
				sessionData.setOutlier();
			}
			sessionData.endSession();
		}
	}
}
