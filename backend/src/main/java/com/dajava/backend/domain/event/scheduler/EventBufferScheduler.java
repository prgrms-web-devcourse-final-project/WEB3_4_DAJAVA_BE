package com.dajava.backend.domain.event.scheduler;



import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.service.EventLogService;
import com.dajava.backend.global.component.buffer.EventBuffer;
import com.dajava.backend.global.component.buffer.EventQueueBuffer;
import com.dajava.backend.global.util.SessionDataKeyUtils;

import lombok.RequiredArgsConstructor;
/**
 * 버퍼 내부 데이터를 처리하는 스케줄러 입니다.
 * 주기적으로 버퍼내 데이터를 리포지드로 전송합니다.
 */
@Component
@RequiredArgsConstructor
public class EventBufferScheduler {

	private static final long INACTIVITY_THRESHOLD_MS = 10 * 60 * 1000; // 10분

	private final EventLogService eventLogService;

	private final EventBuffer eventBuffer;


	/**
	 * 1분 마다 비활성 세션을 감지합니다.
	 * 마지막 로그 기록이 적힌지 10분 이상 경과한 경우 리포지드로 데이터를 저장합니다.
	 * @return void
	 */
	@Scheduled(fixedRate = 60_000) // 1분마다 실행
	public void flushInactiveEventBuffers() {
		long now = System.currentTimeMillis();

		flushInactive(eventBuffer.getClickBuffer(), now);
		flushInactive(eventBuffer.getMoveBuffer(), now);
		flushInactive(eventBuffer.getScrollBuffer(), now);
	}

	/**
	 * 1분 마다 비활성 세션을 감지합니다.
	 * 마지막 로그 기록이 적힌지 10분 이상 경과한 경우 리포지드로 데이터를 저장합니다.
	 * 클릭, 무브, 스크롤 데이터를 정리합니다.
	 * @param buffer 버퍼 내부 이벤트 dto 큐 (EventQueueBuffer<T>)
	 * @param now 현재 시각
	 * @return void
	 */
	private <T> void flushInactive(EventQueueBuffer<T> buffer, long now) {
		for (String key : new ArrayList<>(buffer.getLastUpdatedMap().keySet())) {
			Long lastUpdated = buffer.getLastUpdatedMap().get(key);

			if (lastUpdated == null || now - lastUpdated < INACTIVITY_THRESHOLD_MS) {
				continue; // 아직 활동 중인 세션
			}

			SessionDataKey sessionKey = SessionDataKeyUtils.parseKey(key);
			if (sessionKey == null) {
				continue;
			}

			List<T> staleEvents = buffer.flushEvents(sessionKey);

			// TODO: 이벤트 저장 (타입에 따라 분기 가능)
			eventLogService.saveAll(staleEvents);

			buffer.getLastUpdatedMap().remove(key); // 마지막 업데이트 제거
		}
	}

	@Scheduled(fixedRate = 60_000) // 1분마다 실행
	public  <T> void flushAllEventBuffers() {
		flushEventLog(eventBuffer.getClickBuffer());
		flushEventLog(eventBuffer.getMoveBuffer());
		flushEventLog(eventBuffer.getScrollBuffer());
	}

	private <T> void flushEventLog(EventQueueBuffer<T> buffer) {
		for (String key : new ArrayList<>(buffer.getLastUpdatedMap().keySet())) {
			Long lastUpdated = buffer.getLastUpdatedMap().get(key);

			SessionDataKey sessionKey = SessionDataKeyUtils.parseKey(key);
			if (sessionKey == null) {
				continue;
			}

			List<T> staleEvents = buffer.flushEvents(sessionKey);

			// TODO: 이벤트 저장 (타입에 따라 분기 가능)
			eventLogService.saveAll(staleEvents);
		}
	}


}
