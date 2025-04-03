package com.dajava.backend.domain.event.es.scheduler.vaildation;

import java.util.List;

import com.dajava.backend.domain.event.entity.SessionData;

public interface EsAnalyzer<T> {
	void analyze(List<T> eventDocuments);

}
