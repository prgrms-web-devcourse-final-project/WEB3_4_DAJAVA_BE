package com.dajava.backend.redis.service;

import com.dajava.backend.domain.event.dto.PointerClickEventRequest;

public interface RedisService {
	void createClickEvent(PointerClickEventRequest clickEventRequest);
}
