package com.dajava.backend.global.component.buffer;

import static com.dajava.backend.global.utils.SessionDataKeyUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import com.dajava.backend.domain.event.dto.SessionDataKey;
import com.dajava.backend.global.utils.SessionDataKeyUtils;

import lombok.Getter;

/**
 * 3가지 로그 데이터를 제네릭 방식으로 저장하는 자료 구조 입니다.
 */
@Getter
public class EventQueueBuffer<T> {
	// Todo.. redis로 저장
	private final Map<String, Queue<T>> bufferMap = new ConcurrentHashMap<>();
	private final Map<String, Long> lastUpdatedMap = new ConcurrentHashMap<>();

	public void addEvent(SessionDataKey sessionDataKey, T event) {
		String key = toKey(sessionDataKey);
		bufferMap.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(event);
		lastUpdatedMap.put(key, System.currentTimeMillis());
	}

	public List<T> getEvents(SessionDataKey sessionDataKey) {
		String key = toKey(sessionDataKey);
		Queue<T> queue = bufferMap.getOrDefault(key, new ConcurrentLinkedQueue<>());
		return new ArrayList<>(queue);
	}

	public List<T> flushEvents(SessionDataKey sessionDataKey) {
		String key = toKey(sessionDataKey);
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

	/**
	 * 이벤트가 있는 모든 세션의 키를 반환합니다.
	 * @return Set 이벤트의 세션 키 반환
	 */
	public Set<String> getActiveKeys() {
		return bufferMap.keySet();
	}

	/**
	 * SessionDataKey 객체로 변환후 Set 으로 반환합니다.
	 * @return Set buffer 의 키 형식에 맞게 변환 및 반환
	 */
	public Set<SessionDataKey> getActiveSessionKeys() {
		return getActiveKeys().stream()
			.map(SessionDataKeyUtils::parseKey)
			.collect(Collectors.toSet());
	}
}

