package com.dajava.backend.domain.log.service;

import com.dajava.backend.domain.event.dto.SessionDataKey;

public interface SessionService {
	void startSession(SessionDataKey sessionDataKey);
	void expireSession(String sessionId);

}
