package com.dajava.backend.global.utils.event;

import com.dajava.backend.domain.log.dto.identifier.SessionIdentifier;
import com.dajava.backend.global.utils.LogUtils;

public class KeyGenerator {
	private static final String EVENT_CACHE_PREFIX = "event:";
	private static final String LAST_UPDATED_PREFIX = "lastUpdated:";

	public static String buildEventKey(SessionIdentifier sessionIdentifier) {
		return EVENT_CACHE_PREFIX + LogUtils.createRedisKey(sessionIdentifier);
	}

	public static String buildLastUpdatedKey(String eventKey) {
		return LAST_UPDATED_PREFIX + eventKey;
	}
}
