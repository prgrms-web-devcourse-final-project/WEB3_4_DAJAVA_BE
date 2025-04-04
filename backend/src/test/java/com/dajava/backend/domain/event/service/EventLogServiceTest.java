package com.dajava.backend.domain.event.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;

import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.global.component.buffer.EventBuffer;

/*
 * 로그 데이터를 리포지드에 저장하는 로그 서비스 단위 테스트 입니다.
 *
 * @author NohDongHui
 * @since 2025-03-24
 */
public class EventLogServiceTest {

	private SessionDataRepository sessionDataRepository;
	private SessionDataService sessionDataService;
	private EventBuffer eventBuffer;
	private EventLogService eventLogService;
	private ActivityHandleService activityHandleService;
	private SessionDataDocumentRepository sessionDataDocumentRepository;

	@BeforeEach
	void setUp() {
		sessionDataRepository = mock(SessionDataRepository.class);
		sessionDataService = mock(SessionDataService.class);
		eventBuffer = mock(EventBuffer.class);
		activityHandleService = mock(ActivityHandleService.class);
		sessionDataDocumentRepository = mock(SessionDataDocumentRepository.class);
		eventLogService = new EventLogServiceImpl(sessionDataRepository, sessionDataService, eventBuffer,
			activityHandleService, sessionDataDocumentRepository);
	}

}
