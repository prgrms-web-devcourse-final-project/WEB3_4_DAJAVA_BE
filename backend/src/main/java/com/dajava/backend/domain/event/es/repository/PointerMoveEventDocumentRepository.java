package com.dajava.backend.domain.event.es.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
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

	/**
	 * sessionId에 해당하는 pointerMoveEventDocument를  페이징으로 분할하여 정렬해 가져옴
	 *
	 * @param sessionId , pageable
	 * @return List<PointerMoveEventDocument>
	 */
	Page<PointerMoveEventDocument> findBySessionId(String sessionId, Pageable pageable);

	boolean existsBySessionId(String sessionId);
}
