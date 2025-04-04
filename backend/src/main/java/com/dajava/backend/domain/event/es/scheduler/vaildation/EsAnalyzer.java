package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.util.List;

public interface EsAnalyzer<T> {
	void analyze(List<T> eventDocuments);

}
