package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.util.List;

/**
 * EsAnalyzer를 묶는 인터페이스
 *  @author NohDongHui
 */
public interface EsAnalyzer<T> {
	void analyze(List<T> eventDocuments);

}
