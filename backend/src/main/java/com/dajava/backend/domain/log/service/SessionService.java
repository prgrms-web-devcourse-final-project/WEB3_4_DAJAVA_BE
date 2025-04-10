package com.dajava.backend.domain.log.service;

import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

public interface SessionService {
	void startSession( SessionIdentifier sessionIdentifier);

	void expireSession(String sessionId);

}
