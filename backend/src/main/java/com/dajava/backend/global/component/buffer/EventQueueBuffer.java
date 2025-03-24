package com.dajava.backend.global.component.buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventQueueBuffer<T> {

	private final Map<String, Queue<T>> bufferMap = new ConcurrentHashMap<>();
	private final Map<String, Long> lastUpdatedMap = new ConcurrentHashMap<>();

	private String getKey(String pageUrl, String memberSerialNumber) {
		return pageUrl + "|" + memberSerialNumber;
	}

	public void addEvent(String pageUrl, String memberSerialNumber, T event) {
		String key = getKey(pageUrl, memberSerialNumber);
		bufferMap.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(event);
		lastUpdatedMap.put(key, System.currentTimeMillis());
	}

	public List<T> getEvents(String pageUrl, String memberSerialNumber) {
		String key = getKey(pageUrl, memberSerialNumber);
		Queue<T> queue = bufferMap.getOrDefault(key, new ConcurrentLinkedQueue<>());
		return new ArrayList<>(queue);
	}

	public List<T> flushEvents(String pageUrl, String memberSerialNumber) {
		String key = getKey(pageUrl, memberSerialNumber);
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
