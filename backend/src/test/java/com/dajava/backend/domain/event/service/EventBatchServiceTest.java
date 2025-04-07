package com.dajava.backend.domain.event.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.PointerClickEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerMoveEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.PointerScrollEventDocumentRepository;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.repository.PointerClickEventRepository;
import com.dajava.backend.domain.event.repository.PointerMoveEventRepository;
import com.dajava.backend.domain.event.repository.PointerScrollEventRepository;
import com.dajava.backend.domain.event.repository.SessionDataRepository;
import com.dajava.backend.global.component.buffer.EventBuffer;

@ExtendWith(MockitoExtension.class)
public class EventBatchServiceTest {
	private static final String TEST_MEMBER_SERIAL_NUMBER = "5_team_testSerial";

	@Mock
	private EventBuffer eventBuffer;

	@Mock
	private SessionDataService sessionDataService;

	@Mock
	private PointerClickEventRepository clickRepository;

	@Mock
	private PointerMoveEventRepository moveRepository;

	@Mock
	private PointerScrollEventRepository scrollRepository;

	@Mock
	private SessionDataRepository sessionDataRepository;

	private EventBatchService eventBatchService;

	private ActivityHandleService activityHandleService;

	@Mock
	private PointerClickEventDocumentRepository pointerClickEventDocumentRepository;

	@Mock
	private PointerMoveEventDocumentRepository pointerMoveEventDocumentRepository;

	@Mock
	private PointerScrollEventDocumentRepository pointerScrollEventDocumentRepository;

	@Mock
	private SessionDataDocumentRepository sessionDataDocumentRepository;

	@BeforeEach
	void setUp() {
		eventBatchService = new EventBatchService(
			eventBuffer,
			sessionDataService,
			clickRepository,
			moveRepository,
			scrollRepository,
			sessionDataRepository,
			pointerClickEventDocumentRepository,
			pointerMoveEventDocumentRepository,
			pointerScrollEventDocumentRepository,
			sessionDataDocumentRepository

		);

		activityHandleService = new ActivityHandleService(eventBatchService);
	}

	@Test
	@DisplayName("1. 세션이 활성 상태인지 처리 테스트")
	void t001() {
		// given
		SessionDataKey key = new SessionDataKey("session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER);
		SessionData sessionData = SessionData.create("session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER);
		SessionDataDocument sessionDataDocument = SessionDataDocument.create("sessionDocument1", "member1", "https://example.com", System.currentTimeMillis());

		List<PointerClickEventRequest> clickEvents = Collections.singletonList(
			new PointerClickEventRequest("user1", "session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER,
				System.currentTimeMillis(), 1920, 100, 200, 0, 1500, 500, "div")
		);

		when(eventBuffer.getClickEvents(key)).thenReturn(clickEvents);
		when(eventBuffer.getMoveEvents(key)).thenReturn(Collections.emptyList());
		when(eventBuffer.getScrollEvents(key)).thenReturn(Collections.emptyList());
		when(eventBuffer.flushClickEvents(key)).thenReturn(clickEvents);
		when(sessionDataService.createOrFindSessionData(key)).thenReturn(sessionData);
		when(sessionDataService.createOrFindSessionDataDocument(key)).thenReturn(sessionDataDocument);

		// when
		activityHandleService.processActiveBatchForSession(key);

		// then
		verify(sessionDataService, times(1)).createOrFindSessionData(key);
		verify(sessionDataService, times(1)).createOrFindSessionDataDocument(key);
		verify(clickRepository, times(1)).saveAll(anyList());
		verify(pointerClickEventDocumentRepository, times(1)).saveAll(anyList());
		verify(sessionDataRepository, times(1)).save(sessionData);
		verify(sessionDataService, never()).removeFromCache(key);
		verify(sessionDataService, never()).removeFromEsCache(key);
		verify(sessionDataDocumentRepository, times(1)).save(sessionDataDocument);

	}

	@Test
	@DisplayName("2. 세션을 비활성 상태 처리시 캐시에서 제거 테스트")
	void t002() {
		// given
		SessionDataKey key = new SessionDataKey("session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER);
		SessionData sessionData = SessionData.create("session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER);
		SessionDataDocument sessionDataDocument = SessionDataDocument.create("sessionDocument1", "member1", "https://example.com", System.currentTimeMillis());

		List<PointerClickEventRequest> clickEvents = Collections.emptyList();
		List<PointerMoveEventRequest> moveEvents = Collections.singletonList(
			new PointerMoveEventRequest("user1", "session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER,
				System.currentTimeMillis(), 1920, 300, 400, 0, 1500, 500)
		);
		List<PointerScrollEventRequest> scrollEvents = Collections.emptyList();

		when(eventBuffer.getClickEvents(key)).thenReturn(clickEvents);
		when(eventBuffer.getMoveEvents(key)).thenReturn(moveEvents);
		when(eventBuffer.getScrollEvents(key)).thenReturn(scrollEvents);
		when(eventBuffer.flushClickEvents(key)).thenReturn(clickEvents);
		when(eventBuffer.flushMoveEvents(key)).thenReturn(moveEvents);
		when(eventBuffer.flushScrollEvents(key)).thenReturn(scrollEvents);
		when(sessionDataService.createOrFindSessionData(key)).thenReturn(sessionData);
		when(sessionDataService.createOrFindSessionDataDocument(key)).thenReturn(sessionDataDocument);

		// when
		activityHandleService.processInactiveBatchForSession(key);

		// then
		verify(sessionDataService, times(1)).createOrFindSessionDataDocument(key);
		verify(sessionDataService, times(1)).createOrFindSessionData(key);
		verify(moveRepository, times(1)).saveAll(anyList());
		verify(pointerMoveEventDocumentRepository, times(1)).saveAll(anyList());
		verify(sessionDataRepository, times(1)).save(sessionData);
		verify(sessionDataDocumentRepository, times(1)).save(sessionDataDocument);
		verify(sessionDataService, times(1)).removeFromCache(key);
		verify(sessionDataService, times(1)).removeFromEsCache(key);
	}

	@Test
	@DisplayName("3. 이벤트가 없을 경우 처리되지 않는지 테스트")
	void t003() {
		// given
		SessionDataKey key = new SessionDataKey("session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER);

		when(eventBuffer.getClickEvents(key)).thenReturn(Collections.emptyList());
		when(eventBuffer.getMoveEvents(key)).thenReturn(Collections.emptyList());
		when(eventBuffer.getScrollEvents(key)).thenReturn(Collections.emptyList());

		// when
		eventBatchService.processBatchForSession(key, true);

		// then
		verify(sessionDataService, never()).createOrFindSessionData(any());
		verify(clickRepository, never()).saveAll(anyList());
		verify(moveRepository, never()).saveAll(anyList());
		verify(scrollRepository, never()).saveAll(anyList());
		verify(sessionDataRepository, never()).save(any());
		verify(pointerClickEventDocumentRepository, never()).saveAll(anyList());
		verify(pointerMoveEventDocumentRepository, never()).saveAll(anyList());
		verify(pointerScrollEventDocumentRepository, never()).saveAll(anyList());
	}

	@Test
	@DisplayName("4. 모든 타입의 이벤트를 처리하는지 테스트")
	void t004() {
		// given
		SessionDataKey key = new SessionDataKey("session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER);
		SessionData sessionData = SessionData.create("session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER);
		SessionDataDocument sessionDataDocument = SessionDataDocument.create("sessionDocument1", "member1", "https://example.com", System.currentTimeMillis());

		List<PointerClickEventRequest> clickEvents = Collections.singletonList(
			new PointerClickEventRequest("user1", "session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER,
				System.currentTimeMillis(), 1920, 100, 200, 0, 1500, 500, "div")
		);
		List<PointerMoveEventRequest> moveEvents = Collections.singletonList(
			new PointerMoveEventRequest("user2", "session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER,
				System.currentTimeMillis(), 1920, 300, 400, 0, 1500, 500)
		);
		List<PointerScrollEventRequest> scrollEvents = Collections.singletonList(
			new PointerScrollEventRequest("user3", "session1", "https://example.com", TEST_MEMBER_SERIAL_NUMBER,
				System.currentTimeMillis(), 1920, 0, 1500, 500)
		);

		when(eventBuffer.getClickEvents(key)).thenReturn(clickEvents);
		when(eventBuffer.getMoveEvents(key)).thenReturn(moveEvents);
		when(eventBuffer.getScrollEvents(key)).thenReturn(scrollEvents);
		when(eventBuffer.flushClickEvents(key)).thenReturn(clickEvents);
		when(eventBuffer.flushMoveEvents(key)).thenReturn(moveEvents);
		when(eventBuffer.flushScrollEvents(key)).thenReturn(scrollEvents);
		when(sessionDataService.createOrFindSessionData(key)).thenReturn(sessionData);
		when(sessionDataService.createOrFindSessionDataDocument(key)).thenReturn(sessionDataDocument);

		// when
		eventBatchService.processBatchForSession(key, false);

		// then
		verify(sessionDataService, times(1)).createOrFindSessionData(key);
		verify(sessionDataService, times(1)).createOrFindSessionDataDocument(key);
		verify(clickRepository, times(1)).saveAll(anyList());
		verify(moveRepository, times(1)).saveAll(anyList());
		verify(scrollRepository, times(1)).saveAll(anyList());
		verify(sessionDataRepository, times(1)).save(sessionData);
		verify(pointerClickEventDocumentRepository, times(1)).saveAll(anyList());
		verify(pointerMoveEventDocumentRepository, times(1)).saveAll(anyList());
		verify(pointerScrollEventDocumentRepository, times(1)).saveAll(anyList());
		verify(sessionDataDocumentRepository, times(1)).save(sessionDataDocument);

	}
}
