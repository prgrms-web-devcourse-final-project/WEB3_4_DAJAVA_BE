package com.dajava.backend.domain.event.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dajava.backend.domain.event.es.entity.PointerClickEventDocument;

/**
 * 클릭 이벤트를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface PointerClickEventDocumentRepository
	extends ElasticsearchRepository<PointerClickEventDocument, String> {
}
