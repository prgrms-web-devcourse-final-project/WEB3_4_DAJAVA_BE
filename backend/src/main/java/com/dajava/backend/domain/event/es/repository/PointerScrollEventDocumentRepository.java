package com.dajava.backend.domain.event.es.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;

/**
 * 스크롤 이벤트를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface PointerScrollEventDocumentRepository
	extends ElasticsearchRepository<PointerScrollEventDocument, String> {

	List<PointerScrollEventDocument> findBySessionId(String sessionId);

	List<PointerScrollEventDocument> findBySessionId(String sessionId, Sort sort);
}
