package com.dajava.backend.global.component.buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.dajava.backend.domain.event.dto.SessionDataKey;

public class EventQueueBuffer<T> {

	private final Map<String, Queue<T>> bufferMap = new ConcurrentHashMap<>();
	private final Map<String, Long> lastUpdatedMap = new ConcurrentHashMap<>();

	private String getKey(SessionDataKey sessionDataKey) {
		return sessionDataKey.pageUrl() + "|" + sessionDataKey.memberSerialNumber() + "|" + sessionDataKey.sessionId();
	}

	public void addEvent(SessionDataKey sessionDataKey, T event) {
		String key = getKey(sessionDataKey);
		bufferMap.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(event);
		lastUpdatedMap.put(key, System.currentTimeMillis());
	}

	public List<T> getEvents(SessionDataKey sessionDataKey) {
		String key = getKey(sessionDataKey);
		Queue<T> queue = bufferMap.getOrDefault(key, new ConcurrentLinkedQueue<>());
		return new ArrayList<>(queue);
	}

	public List<T> flushEvents(SessionDataKey sessionDataKey) {
		String key = getKey(sessionDataKey);
		Queue<T> queue = bufferMap.remove(key);
		if (queue == null) {
			return Collections.emptyList();
		}

		List<T> events = new ArrayList<>();
		while (!queue.isEmpty()) {
			events.add(queue.poll());
		}
		lastUpdatedMap.remove(key);
		return events;
	}

	public void clearAll() {
		bufferMap.clear();
		lastUpdatedMap.clear();
	}
}
