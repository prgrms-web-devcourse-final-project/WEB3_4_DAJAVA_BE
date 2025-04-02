package com.dajava.backend.domain.event.es.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;

import com.dajava.backend.domain.event.entity.SessionData;
import com.dajava.backend.domain.event.es.entity.SessionDataDocument;

/**
 * 세션 데이터를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface SessionDataDocumentRepository
	extends ElasticsearchRepository<SessionDataDocument, String> {


    @Query("""
        {
          "bool": {
            "must": [
              { "term": { "pageUrl.keyword": "?0" }},
              { "term": { "sessionId.keyword": "?1" }},
              { "term": { "memberSerialNumber": ?2 }}
            ]
          }
        }
        """)
	Optional<SessionDataDocument> findByPageUrlAndSessionIdAndMemberSerialNumber(
		String pageUrl, String sessionId, String memberSerialNumber
	);

	Optional<SessionDataDocument> findBySessionId(String sessionId);
}
