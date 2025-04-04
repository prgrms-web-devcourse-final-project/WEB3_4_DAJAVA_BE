package com.dajava.backend.domain.event.es.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;

/**
 * 무브 이벤트를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface PointerMoveEventDocumentRepository
	extends ElasticsearchRepository<PointerMoveEventDocument, String> {

	/**
	 * sessionId에 해당하는 pointerMoveEventDocument를 정렬해 가져옴
	 *
	 * @param sessionId
	 * @return List<PointerMoveEventDocument>
	 */
	List<PointerMoveEventDocument> findBySessionId(String sessionId, Sort sort);
}
