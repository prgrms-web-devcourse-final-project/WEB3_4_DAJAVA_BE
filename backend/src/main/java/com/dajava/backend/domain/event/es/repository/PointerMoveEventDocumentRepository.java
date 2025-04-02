package com.dajava.backend.domain.event.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dajava.backend.domain.event.es.entity.PointerMoveEventDocument;

/**
 * 무브 이벤트를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface PointerMoveEventDocumentRepository
	extends ElasticsearchRepository<PointerMoveEventDocument, String> {
}
