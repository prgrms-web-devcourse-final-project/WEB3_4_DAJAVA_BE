package com.dajava.backend.domain.event.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dajava.backend.domain.event.PointerClickEvent;
import com.dajava.backend.domain.event.PointerMoveEvent;
import com.dajava.backend.domain.event.PointerScrollEvent;
import com.dajava.backend.domain.event.SessionData;
import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.global.component.buffer.EventBuffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EventLogServiceImpl
 * EventLogService 인터페이스 구현체
 * 각 이벤트를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
 *
 * @author NohDongHui, Metronon
 * @since 2025-03-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventLogServiceImpl implements EventLogService {
	private final SessionDataRepository sessionDataRepository;
	private final SessionDataService sessionDataService;
	private final EventBuffer eventBuffer;

	/**
	 * 제네릭 형으로 저장된 버퍼의 클릭, 무브, 스크롤 이벤트 리스트를 저장합니다.
	 * 데이터 형을 검사해 각 다른 리포지드 엔티티로 변환해 sessionData 에 저장합니다.
	 * @param events 이벤트 dto 리스트 (List<T>)
	 */
	@Override
	public <T> void saveAll(List<T> events) {
		if (events == null || events.isEmpty()) {
			return;
		}

		Object first = events.getFirst();

		if (first instanceof PointerClickEventRequest clickEvent) {
			saveClickEvents((List<PointerClickEventRequest>)events);
		} else if (first instanceof PointerMoveEventRequest moveEvent) {
			saveMoveEvents((List<PointerMoveEventRequest>)events);
		} else if (first instanceof PointerScrollEventRequest scrollEvent) {
			saveScrollEvents((List<PointerScrollEventRequest>)events);
		}
	}

	/**
	 * 클릭 이벤트 dto 로 클릭 이벤트 엔티티를 생성해 세션데이터에 연관관계를 추가합니다.
	 * create 메서드 내부에서 연관관계를 설정합니다.
	 * 리스트 dto 중 매핑되는 세션 개체가 없는 경우 해당 dto 는 처리하지 않고 다음 데이터로 넘어갑니다.
	 * @param events 이벤트 dto 리스트
	 */
	@Override
	public void saveClickEvents(List<PointerClickEventRequest> events) {
		for (PointerClickEventRequest e : events) {

			Optional<SessionData> optionalSession = sessionDataRepository
				.findByPageUrlAndSessionIdAndMemberSerialNumber(
					e.pageUrl(), e.sessionId(), e.memberSerialNumber()
				);

			if (optionalSession.isEmpty()) {
				continue;
			}

			SessionData session = optionalSession.get();
			PointerClickEvent entity = PointerClickEvent.create(
				e.clientX(), e.clientY(), e.pageUrl(), e.browserWidth(),
				e.sessionId(), e.memberSerialNumber(), session
			);

			sessionDataRepository.save(session); // cascade 로 이벤트도 저장됨
		}
	}

	/**
	 * 무브 이벤트 dto 로 무브 이벤트 엔티티를 생성해 세션데이터에 연관관계를 추가합니다.
	 * create 메서드 내부에서 연관관계를 설정합니다.
	 * 리스트 dto 중 매핑되는 세션 개체가 없는 경우 해당 dto 는 처리하지 않고 다음 데이터로 넘어갑니다.
	 * @param events 이벤트 dto 리스트
	 */
	@Override
	public void saveMoveEvents(List<PointerMoveEventRequest> events) {
		for (PointerMoveEventRequest e : events) {

			Optional<SessionData> optionalSession = sessionDataRepository
				.findByPageUrlAndSessionIdAndMemberSerialNumber(
					e.pageUrl(), e.sessionId(), e.memberSerialNumber()
				);

			if (optionalSession.isEmpty()) {
				continue;
			}

			SessionData session = optionalSession.get();
			PointerMoveEvent entity = PointerMoveEvent.create(
				e.clientX(), e.clientY(), e.pageUrl(), e.browserWidth(),
				e.sessionId(), e.memberSerialNumber(), session
			);

			sessionDataRepository.save(session);
		}
	}

	/**
	 * 스크롤 이벤트 dto 로 스크롤 이벤트 엔티티를 생성해 세션데이터에 연관관계를 추가합니다.
	 * create 메서드 내부에서 연관관계를 설정합니다.
	 * 리스트 dto 중 매핑되는 세션 개체가 없는 경우 해당 dto 는 처리하지 않고 다음 데이터로 넘어갑니다.
	 * @param events 이벤트 dto 리스트
	 * @return void
	 */
	@Override
	public void saveScrollEvents(List<PointerScrollEventRequest> events) {
		for (PointerScrollEventRequest e : events) {

			Optional<SessionData> optionalSession = sessionDataRepository
				.findByPageUrlAndSessionIdAndMemberSerialNumber(
					e.pageUrl(), e.sessionId(), e.memberSerialNumber()
				);

			if (optionalSession.isEmpty()) {
				continue;
			}

			SessionData session = optionalSession.get();
			PointerScrollEvent entity = PointerScrollEvent.create(
				e.scrollY(), e.pageUrl(), e.browserWidth(),
				e.sessionId(), e.memberSerialNumber(), session
			);

			sessionDataRepository.save(session);
		}
	}

	/**
	 * 클릭 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 */
	@Override
	@Transactional
	public void createClickEvent(PointerClickEventRequest request) {
		log.info("클릭 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);

		// SessionData 를 통해 Cache 확인, 없으면 생성
		sessionDataService.createOrFindSessionData(sessionDataKey);

		// 클릭 이벤트 버퍼링
		eventBuffer.addClickEvent(request, sessionDataKey);
	}

	/**
	 * 무브 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 */
	@Override
	@Transactional
	public void createMoveEvent(PointerMoveEventRequest request) {
		log.info("이동 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);

		// SessionData 를 통해 Cache 확인, 없으면 생성
		sessionDataService.createOrFindSessionData(sessionDataKey);

		// 이동 이벤트 버퍼링
		eventBuffer.addMoveEvent(request, sessionDataKey);
	}

	/**
	 * 스크롤 이벤트 DTO 를 통해 sessionDataKey 를 발급하고, 버퍼에 담습니다.
	 */
	@Override
	@Transactional
	public void createScrollEvent(PointerScrollEventRequest request) {
		log.info("스크롤 이벤트 로깅: {}", request);

		SessionDataKey sessionDataKey = new SessionDataKey(
			request.sessionId(), request.pageUrl(), request.memberSerialNumber()
		);

		// SessionData 를 통해 Cache 확인, 없으면 생성
		sessionDataService.createOrFindSessionData(sessionDataKey);

		// 스크롤 이벤트 버퍼링
		eventBuffer.addScrollEvent(request, sessionDataKey);
	}
}


