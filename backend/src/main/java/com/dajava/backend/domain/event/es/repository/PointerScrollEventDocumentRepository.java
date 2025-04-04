package com.dajava.backend.domain.event.es.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;

/**
 * 스크롤 이벤트를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface PointerScrollEventDocumentRepository
	extends ElasticsearchRepository<PointerScrollEventDocument, String> {

	/**
	 * sessionId에 해당하는 pointerScrollEventDocument를 정렬해 가져옴
	 *
	 * @param sessionId
	 * @return List<PointerScrollEventDocument>
	 */
	List<PointerScrollEventDocument> findBySessionId(String sessionId, Sort sort);
}
