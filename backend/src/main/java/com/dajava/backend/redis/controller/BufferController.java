package com.dajava.backend.redis.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.SessionDataKey;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/debug/buffer")
public class BufferController {
	private final EventQueueRedisBuffer<PointerClickEventRequest> eventQueueBuffer;

	@GetMapping("/{projectId}/{userId}/{pageId}")
	public List<PointerClickEventRequest> getBuffer(@PathVariable String projectId,
		@PathVariable String userId,
		@PathVariable String pageId) {
		SessionDataKey key = new SessionDataKey(projectId, userId, pageId);
		return eventQueueBuffer.getEvents(key);
	}
}
