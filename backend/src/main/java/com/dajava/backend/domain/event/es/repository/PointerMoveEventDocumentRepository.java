package com.dajava.backend.domain.event.es.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;
import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;

/**
 * 무브 이벤트를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface PointerMoveEventDocumentRepository
	extends ElasticsearchRepository<PointerMoveEventDocument, String> {

	List<PointerMoveEventDocument> findBySessionId(String sessionId);

	List<PointerMoveEventDocument> findBySessionId(String sessionId, Sort sort);
}
