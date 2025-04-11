package com.dajava.backend.global.utils.event;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EventSerializer<T> {
	private final ObjectMapper objectMapper;
	private final Class<T> clazz;

	public EventSerializer(ObjectMapper objectMapper, Class<T> clazz) {
		this.objectMapper = objectMapper;
		this.clazz = clazz;
	}

	public String serialize(T event) throws Exception {
		return objectMapper.writeValueAsString(event);
	}

	public T deserialize(String json) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			return null;
		}
	}
}