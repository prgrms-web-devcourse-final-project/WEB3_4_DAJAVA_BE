package com.dajava.backend.domain.log.service;

public interface SessionService {
	void expireSession(String sessionId);
}
