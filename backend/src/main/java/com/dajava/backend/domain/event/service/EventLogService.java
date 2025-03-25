package com.dajava.backend.domain.event.service;

import java.util.List;

import com.dajava.backend.domain.event.SessionData;
import com.dajava.backend.domain.event.dto.PointerClickEventRequest;
import com.dajava.backend.domain.event.dto.PointerMoveEventRequest;
import com.dajava.backend.domain.event.dto.PointerScrollEventRequest;

/**
 * EventLogService
 * EventLog Domain 의 비즈니스 로직을 처리하는 인터페이스
 *
 * @author NohDongHui, Metronon
 * @since 2025-03-24
 */
public interface EventLogService {

	public <T> void saveAll(List<T> events);

	public void saveClickEvents(List<PointerClickEventRequest> events);

	public void saveMoveEvents(List<PointerMoveEventRequest> events);

	public void saveScrollEvents(List<PointerScrollEventRequest> events);

	void createClickEvent(PointerClickEventRequest clickEventRequest);

	void createMoveEvent(PointerMoveEventRequest moveEventRequest);

	void createScrollEvent(PointerScrollEventRequest scrollEventRequest);
}
