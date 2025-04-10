package com.dajava.backend.domain.log.handler;

import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

@FunctionalInterface
public interface EventHandler<T> {
	void handle(T request, SessionIdentifier sessionIdentifier);
}