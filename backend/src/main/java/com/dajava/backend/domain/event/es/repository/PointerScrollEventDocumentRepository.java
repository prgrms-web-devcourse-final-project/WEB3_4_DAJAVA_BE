package com.dajava.backend.domain.event.es.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dajava.backend.domain.event.es.entity.PointerScrollEventDocument;

/**
 * 스크롤 이벤트를 저장하는 ES 인덱스 입니다.
 *  @author NohDongHui
 */
public interface PointerScrollEventDocumentRepository
	extends ElasticsearchRepository<PointerScrollEventDocument, String> {
}
