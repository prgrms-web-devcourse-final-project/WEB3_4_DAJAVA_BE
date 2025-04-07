package com.dajava.backend.domain.event.es.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dajava.backend.domain.event.es.entity.SolutionEventDocument;
import com.dajava.backend.domain.event.es.repository.SolutionEventDocumentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SolutionEventDocumentRepository 인터페이스 구현체
 *
 * @author NohDongHui
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SolutionEventDocumentServiceImpl implements SolutionEventDocumentService {

	private final SolutionEventDocumentRepository solutionEventDocumentRepository;

	@Override
	public void saveAllSolutionEvents(List<SolutionEventDocument> events) {
		if (events == null || events.isEmpty()) {
			log.warn("저장할 SolutionEvent가 없습니다.");
			return;
		}
		solutionEventDocumentRepository.saveAll(events);
		log.debug("SolutionEvent {}건 저장 완료", events.size());
	}
}
