package com.dajava.backend.domain.event.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;
import com.dajava.backend.domain.event.repository.SessionDataRepository;

@ExtendWith(MockitoExtension.class)
public class SessionDataServiceTest {

	@Mock
	private SessionDataRepository sessionDataRepository;

	@Mock
	private SessionDataDocumentRepository sessionDataDocumentRepository;

	private SessionDataService sessionDataService;

	@BeforeEach
	void setUp() {
		sessionDataService = new SessionDataService(sessionDataRepository, sessionDataDocumentRepository);
	}

	@Test
	@DisplayName("1. 세션이 존재하면 기존 세션을 반환하는지 테스트")
	void t001() {
		// given
		String sessionId = "session1";
		String pageUrl = "https://example.com";
		String memberSerialNumber = "user001";

		SessionDataKey key = new SessionDataKey(sessionId, pageUrl, memberSerialNumber);

		SessionData existingSession = SessionData.builder()
			.sessionId(sessionId)
			.pageUrl(pageUrl)
			.memberSerialNumber(memberSerialNumber)
			.isOutlier(false)
			.isMissingValue(false)
			.isVerified(true)
			.build();

		when(sessionDataRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
			sessionId, pageUrl, memberSerialNumber
		)).thenReturn(Optional.of(existingSession));

		// when
		SessionData result = sessionDataService.createOrFindSessionData(key);

		// then
		assertThat(result).isEqualTo(existingSession);
		verify(sessionDataRepository, times(1))
			.findByPageUrlAndSessionIdAndMemberSerialNumber(sessionId, pageUrl, memberSerialNumber);
		verify(sessionDataRepository, times(0)).save(any());

		// 동일한 키로 다시 호출하면 캐시에서 가져와야 함
		sessionDataService.createOrFindSessionData(key);
		verify(sessionDataRepository, times(1))  // 추가 조회 없음
			.findByPageUrlAndSessionIdAndMemberSerialNumber(sessionId, pageUrl, memberSerialNumber);
	}

	@Test
	@DisplayName("2. 세션이 존재하지 않으면 새 세션을 생성하는지 테스트")
	void t002() {
		// given
		String sessionId = "session2";
		String pageUrl = "https://example.com";
		String memberSerialNumber = "user002";

		SessionDataKey key = new SessionDataKey(sessionId, pageUrl, memberSerialNumber);

		when(sessionDataRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
			sessionId, pageUrl, memberSerialNumber
		)).thenReturn(Optional.empty());

		SessionData newSession = SessionData.builder()
			.sessionId(sessionId)
			.pageUrl(pageUrl)
			.memberSerialNumber(memberSerialNumber)
			.isOutlier(false)
			.isMissingValue(false)
			.isVerified(false)
			.build();

		when(sessionDataRepository.save(any(SessionData.class))).thenReturn(newSession);

		// when
		SessionData result = sessionDataService.createOrFindSessionData(key);

		// then
		ArgumentCaptor<SessionData> sessionCaptor = ArgumentCaptor.forClass(SessionData.class);
		verify(sessionDataRepository, times(1))
			.findByPageUrlAndSessionIdAndMemberSerialNumber(sessionId, pageUrl, memberSerialNumber);
		verify(sessionDataRepository, times(1)).save(sessionCaptor.capture());

		SessionData capturedSession = sessionCaptor.getValue();
		assertThat(capturedSession.getSessionId()).isEqualTo(sessionId);
		assertThat(capturedSession.getPageUrl()).isEqualTo(pageUrl);
		assertThat(capturedSession.getMemberSerialNumber()).isEqualTo(memberSerialNumber);
		assertThat(capturedSession.isOutlier()).isFalse();
		assertThat(capturedSession.isMissingValue()).isFalse();
		assertThat(capturedSession.isVerified()).isFalse();
	}

	@Test
	@DisplayName("3. 캐시에서 세션 제거 테스트")
	void t003() {
		// given
		SessionDataKey key = new SessionDataKey("session1", "https://example.com", "user001");
		SessionData sessionData = SessionData.create("session1", "https://example.com", "user001");

		// 먼저 캐시에 세션 데이터 추가
		when(sessionDataRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
			key.sessionId(), key.pageUrl(), key.memberSerialNumber()
		)).thenReturn(Optional.of(sessionData));

		sessionDataService.createOrFindSessionData(key);

		// 캐시에서 확인을 위해 저장소 접근 모의 객체 초기화
		when(sessionDataRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
			key.sessionId(), key.pageUrl(), key.memberSerialNumber()
		)).thenReturn(Optional.of(sessionData));

		// when
		sessionDataService.removeFromCache(key);

		// then
		// 캐시에서 제거된 후 다시 접근하면 저장소를 다시 조회해야 함
		sessionDataService.createOrFindSessionData(key);
		verify(sessionDataRepository, times(2))  // 초기 + 캐시 제거 후 = 2번
			.findByPageUrlAndSessionIdAndMemberSerialNumber(
				key.sessionId(), key.pageUrl(), key.memberSerialNumber());
	}

	@Test
	@DisplayName("4. ES 세션 문서가 존재하면 기존 문서를 반환하는지 테스트")
	void t004() {
		// given
		String sessionId = "session_es_1";
		String pageUrl = "https://example.com";
		String memberSerialNumber = "es_user001";
		SessionDataKey key = new SessionDataKey(sessionId, pageUrl, memberSerialNumber);

		SessionDataDocument existingDocument = SessionDataDocument.builder()
			.id(sessionId + pageUrl + memberSerialNumber)
			.pageUrl(pageUrl)
			.memberSerialNumber(memberSerialNumber)
			.timestamp(System.currentTimeMillis())
			.build();

		when(sessionDataDocumentRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
			pageUrl, sessionId, memberSerialNumber
		)).thenReturn(Optional.of(existingDocument));

		// when
		SessionDataDocument result = sessionDataService.createOrFindSessionDataDocument(key);

		// then
		assertThat(result).isEqualTo(existingDocument);
		verify(sessionDataDocumentRepository, times(1))
			.findByPageUrlAndSessionIdAndMemberSerialNumber(pageUrl, sessionId, memberSerialNumber);
		verify(sessionDataDocumentRepository, never()).save(any());

		// 캐시에서 다시 조회 시 저장소 조회 없음
		sessionDataService.createOrFindSessionDataDocument(key);
		verify(sessionDataDocumentRepository, times(1))  // 조회는 딱 1번
			.findByPageUrlAndSessionIdAndMemberSerialNumber(pageUrl, sessionId, memberSerialNumber);
	}

	@Test
	@DisplayName("5. ES 세션 문서가 존재하지 않으면 새 문서를 생성하는지 테스트")
	void t005() {
		// given
		String sessionId = "session_es_2";
		String pageUrl = "https://example.com";
		String memberSerialNumber = "es_user002";
		SessionDataKey key = new SessionDataKey(sessionId, pageUrl, memberSerialNumber);

		when(sessionDataDocumentRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
			pageUrl, sessionId, memberSerialNumber
		)).thenReturn(Optional.empty());

		SessionDataDocument newDoc = SessionDataDocument.builder()
			.id(sessionId + pageUrl + memberSerialNumber)
			.pageUrl(pageUrl)
			.memberSerialNumber(memberSerialNumber)
			.timestamp(System.currentTimeMillis())
			.isOutlier(false)
			.isMissingValue(false)
			.isSessionEnded(false)
			.isVerified(false)
			.build();

		when(sessionDataDocumentRepository.save(any())).thenReturn(newDoc);

		// when
		SessionDataDocument result = sessionDataService.createOrFindSessionDataDocument(key);

		// then
		assertThat(result.getPageUrl()).isEqualTo(pageUrl);
		assertThat(result.getMemberSerialNumber()).isEqualTo(memberSerialNumber);
		verify(sessionDataDocumentRepository, times(1))
			.findByPageUrlAndSessionIdAndMemberSerialNumber(pageUrl, sessionId, memberSerialNumber);
		verify(sessionDataDocumentRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("6. ES 캐시에서 문서를 제거하면 다시 저장소를 조회해야 함")
	void t006() {
		// given
		String sessionId = "session_es_3";
		String pageUrl = "https://example.com";
		String memberSerialNumber = "es_user003";
		SessionDataKey key = new SessionDataKey(sessionId, pageUrl, memberSerialNumber);

		SessionDataDocument doc = SessionDataDocument.builder()
			.id(sessionId + pageUrl + memberSerialNumber)
			.pageUrl(pageUrl)
			.memberSerialNumber(memberSerialNumber)
			.timestamp(System.currentTimeMillis())
			.build();

		when(sessionDataDocumentRepository.findByPageUrlAndSessionIdAndMemberSerialNumber(
			pageUrl, sessionId, memberSerialNumber
		)).thenReturn(Optional.of(doc));

		// 1차 호출 → 캐시에 들어감
		sessionDataService.createOrFindSessionDataDocument(key);

		// 2차 호출 → 캐시 사용 (저장소 조회 없음)
		sessionDataService.createOrFindSessionDataDocument(key);

		verify(sessionDataDocumentRepository, times(1))
			.findByPageUrlAndSessionIdAndMemberSerialNumber(pageUrl, sessionId, memberSerialNumber);

		// 캐시 제거
		sessionDataService.removeFromEsCache(key);

		// 3차 호출 → 다시 저장소 조회해야 함
		sessionDataService.createOrFindSessionDataDocument(key);

		verify(sessionDataDocumentRepository, times(2))
			.findByPageUrlAndSessionIdAndMemberSerialNumber(pageUrl, sessionId, memberSerialNumber);
	}
}

