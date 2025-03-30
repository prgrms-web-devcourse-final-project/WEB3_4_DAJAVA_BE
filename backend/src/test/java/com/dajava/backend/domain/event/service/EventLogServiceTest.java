package com.dajava.backend.domain.event.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.entity.PointerClickEvent;
import com.dajava.backend.domain.event.entity.SessionData;
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
	private EventBatchService eventBatchService;

	@BeforeEach
	void setUp() {
		sessionDataRepository = mock(SessionDataRepository.class);
		sessionDataService = mock(SessionDataService.class);
		eventBuffer = mock(EventBuffer.class);
		eventBatchService = mock(EventBatchService.class);
		eventLogService = new EventLogServiceImpl(sessionDataRepository, sessionDataService, eventBuffer,
			eventBatchService);
	}


}

