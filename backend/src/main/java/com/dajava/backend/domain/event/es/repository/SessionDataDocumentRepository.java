package com.dajava.backend.domain.event.es.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;

import com.dajava.backend.domain.event.es.entity.SessionDataDocument;

/**
 * 세션 데이터를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface SessionDataDocumentRepository
	extends ElasticsearchRepository<SessionDataDocument, String> {


	/**
	 * url, sessionId, serialNumer로 sessionDataDocument 조회
	 * es 쿼리문으로 조회
	 * @param url, sessionId, serialNumer 솔루션 시리얼 번호
	 * @return Optional<SessionDataDocument>
	 */
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

	/**
	 * IsSessionEnded가 true인 sessionDataDocument를 조회
	 * 한번에 가져오지 않고 페이징해 가져옴
	 * @param pageable
	 * @return Page<SessionDataDocument>
	 */
	Page<SessionDataDocument> findByIsSessionEndedTrue(Pageable pageable);
}
