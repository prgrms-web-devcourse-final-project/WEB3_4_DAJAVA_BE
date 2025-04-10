package com.dajava.backend.global.utils.session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dajava.backend.domain.event.dto.SessionDataKey;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionKeyCollector {
	private static final List<String> EVENT_TYPES = List.of("click:", "move:", "scroll:");
	private final ActiveSessionManager activeSessionManager;

	public Set<SessionDataKey> collectAllActiveSessionKeys() {
		Set<SessionDataKey> allKeys = new HashSet<>();
		for (String type : EVENT_TYPES) {
			allKeys.addAll(activeSessionManager.getActiveSessionKeysForType(type));
		}
		return allKeys;
	}
}
