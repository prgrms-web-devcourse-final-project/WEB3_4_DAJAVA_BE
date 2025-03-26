package com.dajava.backend.domain.event.scheduler.vaildation;

import com.dajava.backend.domain.event.SessionData;

public interface Analyzer {

	boolean analyze(SessionData sessionData);
}
