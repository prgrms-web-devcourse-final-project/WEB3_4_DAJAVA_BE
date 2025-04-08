package com.dajava.backend.redis.service;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;

public interface RedisService {
	void createClickEvent(PointerClickEventRequest clickEventRequest);
	void createMoveEvent(PointerMoveEventRequest pointerMoveEventRequest);
	void createScrollEvent(PointerScrollEventRequest pointerScrollEventRequest);
}
