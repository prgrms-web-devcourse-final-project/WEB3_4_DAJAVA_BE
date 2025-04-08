package com.dajava.backend.domain.event.es.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;
import com.dajava.backend.domain.event.es.repository.SessionDataDocumentRepository;

@ExtendWith(MockitoExtension.class)
public class SessionDataDocumentServiceTest {

	@InjectMocks
	private SessionDataDocumentServiceImpl sessionDataDocumentService;

	@Mock
	private SessionDataDocumentRepository sessionDataDocumentRepository;

	@Test
	@DisplayName("종료된 세션 중 미검증된 문서를 페이징 조회")
	void t1() {
		// given
		int page = 0;
		int size = 2;
		PageRequest pageRequest = PageRequest.of(page, size);

		SessionDataDocument doc1 = SessionDataDocument.builder()
			.sessionId("session1")
			.isSessionEnded(true)
			.isVerified(false)
			.build();

		SessionDataDocument doc2 = SessionDataDocument.builder()
			.sessionId("session2")
			.isSessionEnded(true)
			.isVerified(false)
			.build();

		Page<SessionDataDocument> mockPage = new PageImpl<>(List.of(doc1, doc2), pageRequest, 2);
		when(sessionDataDocumentRepository.findByIsSessionEndedTrueAndIsVerifiedFalse(pageRequest))
			.thenReturn(mockPage);

		// when
		Page<SessionDataDocument> result = sessionDataDocumentService.getEndedSessions(page, size);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).containsExactly(doc1, doc2);
		verify(sessionDataDocumentRepository, times(1))
			.findByIsSessionEndedTrueAndIsVerifiedFalse(pageRequest);
	}
}
