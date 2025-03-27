package com.dajava.backend.domain.event.scheduler.vaildation;

import java.util.List;

import com.dajava.backend.domain.event.PointerClickEvent;
import com.dajava.backend.domain.event.SessionData;

public interface Analyzer<T> {
	List<T> analyze(SessionData sessionData);
}
