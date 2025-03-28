package com.dajava.backend.domain.event.scheduler.vaildation;

import java.util.List;

import com.dajava.backend.domain.event.entity.SessionData;

public interface Analyzer<T> {
	List<T> analyze(SessionData sessionData);
}
