package com.dajava.backend.domain.log.handler;

import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;

@FunctionalInterface
public interface SessionIdentifierExtractor<T> {
	SessionIdentifier extract(T request);
}
