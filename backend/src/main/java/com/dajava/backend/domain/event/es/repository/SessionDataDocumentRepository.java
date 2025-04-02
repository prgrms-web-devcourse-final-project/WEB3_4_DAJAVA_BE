package com.dajava.backend.domain.event.es.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;

/**
 * 세션 데이터를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface SessionDataDocumentRepository
	extends ElasticsearchRepository<SessionDataDocument, String> {

	Optional<SessionDataDocument> findByPageUrlAndSessionIdAndMemberSerialNumber(
		String sessionId, String pageUrl, String memberSerialNumber);

	Optional<SessionDataDocument> findBySessionId(String sessionId);
}
