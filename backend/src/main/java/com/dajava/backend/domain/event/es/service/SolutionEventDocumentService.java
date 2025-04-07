package com.dajava.backend.domain.event.es.service;

import java.util.List;

import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;

/**
 *
 * SolutionEventDocumentService 도메인의 서비스 로직을 처리하는 인터페이스
 *
 * @author NohDongHui
 */
public interface SolutionEventDocumentService {

	/**
	 * 모든 SolutionEventDocuments 를 저장
	 * @param events
	 * @return void
	 */
	public void saveAllSolutionEvents(List<SolutionEventDocument> events);
}
